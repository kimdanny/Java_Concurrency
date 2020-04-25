package ProducerConsumer2;

import Thread_and_Runnable.ThreadColour;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import static ProducerConsumer2.Main.EOF;

public class Main {
    public static final String EOF = "EOF";

    public static void main(String args[]){
        // ArrayList is not Thread-safe
        List<String> buffer = new ArrayList<>();
        ReentrantLock bufferLock = new ReentrantLock();
        MyProducer myProducer = new MyProducer(buffer, ThreadColour.ANSI_CYAN, bufferLock);
        MyConsumer myConsumer1 = new MyConsumer(buffer, ThreadColour.ANSI_RED, bufferLock);
        MyConsumer myConsumer2 = new MyConsumer(buffer, ThreadColour.ANSI_BLUE, bufferLock);


        // Three different Threads share one ArrayList instance, which can cause Thread Interference
        // (eg. IndexOutOfBound, Two different consumers trying to remove the same thing, etc.)
        // So we need to synchronize buffer object.

        /*Option 1 to start the thread - normal way*/
//        new Thread(myProducer).start();
//        new Thread(myConsumer1).start();
//        new Thread(myConsumer2).start();

        /*Option 2 to start the thread - with Fixed Thread Pool*/
        int coreCount = Runtime.getRuntime().availableProcessors();
        System.out.println("coreCount = " + coreCount);
        // TODO: See how the outputs differ from the number of Threads allowed in the pool.
        // TODO continued: try 1 for single Thread, many more for multi-thread, and coreCount for theoretically optimal number of threads
        ExecutorService executorService = Executors.newFixedThreadPool(coreCount);

        executorService.execute(myProducer);
        executorService.execute(myConsumer1);
        executorService.execute(myConsumer2);

        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println(ThreadColour.ANSI_PURPLE + "I'm being printed for the callable class");
                return "This is the callable result";
            }
        });

        try{
            System.out.println(future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }
}

// When using Lock interface, since unlocking the lock is not done automatically like synchronization,
// you must make sure that you unlocking-code is executed no matter how.

class MyProducer implements Runnable {
    private List<String> buffer;
    private String color;
    private ReentrantLock bufferLock;

    public MyProducer(List<String> buffer, String color, ReentrantLock bufferLock){
        this.buffer = buffer;
        this.color = color;
        this.bufferLock = bufferLock;
    }

    public void run(){
        Random random = new Random();
        String[] nums = {"1", "2", "3", "4", "5"};

        for (String num : nums){
            try {
                System.out.println(color + "Adding..." + num);
//                synchronized (buffer) {
//                    buffer.add(num);
//                }
                bufferLock.lock();
                try{
                    buffer.add(num);
                }
                finally {
                    bufferLock.unlock();
                }

                Thread.sleep(random.nextInt(1000));
            }
            catch (InterruptedException e){
                System.out.println("Producer was interrupted");
            }
        }
        System.out.println(color + "Adding EOF and exiting");
//        synchronized (buffer){
//            buffer.add("EOF");
//        }
        bufferLock.lock();
        try{
            buffer.add("EOF");
        }
        finally {
            bufferLock.unlock();
        }

    }

}


class MyConsumer implements Runnable{
    private List<String> buffer;
    private String color;
    private ReentrantLock bufferLock;

    public MyConsumer(List<String> buffer, String color, ReentrantLock bufferLock){
        this.buffer = buffer;
        this.color = color;
        this.bufferLock = bufferLock;
    }

    /*
    public void run(){
        while(true){
//            synchronized (buffer) {
            bufferLock.lock();
            try{
                if (buffer.isEmpty()){
                    // doing unlocking here will cause error, since unlock code is in the finally block as well.
                    // below code will try call unlock, but we dont own the lock -> try to release the lock that we dont own --> IllegalMonitorStateException
                    // bufferLock.unlock();
                    continue;
                }
                if (buffer.get(0).equals(EOF)){
                    System.out.println(color + "Exiting");
                    // commenting below as the same reason as above
                    // bufferLock.unlock();
                    break;
                }
                else {
                    System.out.println(color + "Removed" + buffer.remove(0));
                }
            }
            finally {
                bufferLock.unlock();
            }

//            }
        }

    }
    */

    // BELOW IS THE BEST CODE
    public void run(){

        // count how many times tryLock() returns false
        int counter = 0;

        while(true){
            /* tryLock() --> A thread can test whether a lock is available.
            * If lock is available, the thread will acquire the lock and continue executing
            * if it's not available, the thread won't block and alternatively execute different code.
            * */
            if (bufferLock.tryLock()){
                try{
                    if (buffer.isEmpty()){
                        continue;
                    }
                    // buffer is not empty here
                    System.out.println(color + "The counter = " + counter);
                    counter = 0;

                    if (buffer.get(0).equals(EOF)){
                        System.out.println(color + "Exiting");
                        break;
                    }
                    else {
                        System.out.println(color + "Removed" + buffer.remove(0));
                    }
                }
                finally {
                    bufferLock.unlock();
                }
            }
            else {
                counter++;
            }

        }

    }

}
