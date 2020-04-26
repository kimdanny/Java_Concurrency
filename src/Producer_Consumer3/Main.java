package Producer_Consumer3;

/**
 * ArrayBlockingQueue has thread-safe method put() and take()
 * Especially for Producer and Consumer, FIFO (First In First Out) Structured Queue is great to use
 *
 * We are replacing ArrayList with ArrayBlockingQueue.
 * We no longer need ReentrantLock.
 */

import Thread_and_Runnable.ThreadColour;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static Producer_Consumer3.Main.EOF;

public class Main {
    public static final String EOF = "EOF";

    public static void main(String args[]){
        // ArrayList is not Thread-safe
        // List<String> buffer = new ArrayList<>();
        ArrayBlockingQueue<String> buffer = new ArrayBlockingQueue<String>(5);
        // ReentrantLock bufferLock = new ReentrantLock();
        MyProducer myProducer = new MyProducer(buffer, ThreadColour.ANSI_CYAN);
        MyConsumer myConsumer1 = new MyConsumer(buffer, ThreadColour.ANSI_RED);
        MyConsumer myConsumer2 = new MyConsumer(buffer, ThreadColour.ANSI_BLUE);

        int coreCount = Runtime.getRuntime().availableProcessors();
        System.out.println("coreCount = " + coreCount);
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
    // private List<String> buffer;
    private ArrayBlockingQueue<String> buffer;
    private String color;
    // private ReentrantLock bufferLock;

    public MyProducer(ArrayBlockingQueue<String> buffer, String color){
        this.buffer = buffer;
        this.color = color;
    }

    public void run(){
        Random random = new Random();
        String[] nums = {"1", "2", "3", "4", "5"};

        for (String num : nums){
            try {
                System.out.println(color + "Adding..." + num);
                buffer.put(num);

                Thread.sleep(random.nextInt(1000));
            }
            catch (InterruptedException e){
                System.out.println("Producer was interrupted");
            }
        }
        System.out.println(color + "Adding EOF and exiting");

        try {
            buffer.put("EOF");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}


class MyConsumer implements Runnable{
    // private List<String> buffer;
    private ArrayBlockingQueue<String> buffer;
    private String color;
    // private ReentrantLock bufferLock;

    public MyConsumer(ArrayBlockingQueue<String> buffer, String color){
        this.buffer = buffer;
        this.color = color;
        // this.bufferLock = bufferLock;
    }


    public void run(){

        /*
        * < Without synchronizing buffer >
        * In the consumer, we check to see if the queue is empty.
        * If it is not, we then peek at the next element but the consumer thread can be suspended between
        * calling isEmpty() and calling peek().
        * While it suspended the other, consumer thread can run and take the next element from the queue when the suspended consumer thread
        * runs again, peek returns null and consequently, we get a NullPointerException when we call the equals() method.
        *
        * We may still have to add synchronization code when using thread-safe classes like ArrayBlockingQueue.
        * */
        while(true){
            synchronized (buffer){
                try{
                    if (buffer.isEmpty()){
                        continue;
                    }

                    if (buffer.peek().equals(EOF)){
                        System.out.println(color + "Exiting");
                        break;
                    }
                    else {
                        System.out.println(color + "Removed" + buffer.take());
                    }
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

        }

    }

}
