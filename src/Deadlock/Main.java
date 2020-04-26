package Deadlock;

/**
 * Deadlock Example
 *
 * -- Solution 1 Explanation --
 * In solution 1 code, threads are trying to obtain the locks in the same order.
 * Once the thread has obtained the outer lock, it tries to obtain first, in other words
 * only that thread can obtain the inner lock.
 * In other words, it is not possible for one of the threads to hold one lock but not the other lock.
 * This is the key to avoiding deadlocks when two or more threads will be competing for two or more locks.
 * You want to make sure that all threads will try to obtain the locks "in the same order".
 *
 * -- Another solution Explanation --
 * Another solution would be to use a lock object rather than using synchronized blocks,
 * so using the tryLock() method which was dealt in Producer_Consumer2.
 *
 */
public class Main {

    public static Object lock1 = new Object();
    public static Object lock2 = new Object();

    public static void main(String[] args) {
        new Thread1().start();
        new Thread2().start();
    }

    private static class Thread1 extends Thread {
        @Override
        public void run(){
            synchronized (lock1){
                System.out.println("Thread 1: Has lock1");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread 1: Waiting for lock2");

                synchronized (lock2){
                    System.out.println("Thread 1: Has lock1 and lock2");
                }
                System.out.println("Thread 1: Released lock2");
            }
            System.out.println("Thread 1: Released lock1. Exiting...");
        }

    }

    private static class Thread2 extends Thread {
        // BELOW CAUSES DEADLOCK
        @Override
        public void run(){
            synchronized (lock2){
                System.out.println("Thread 2: Has lock2");
                try{
                    Thread.sleep(100);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                System.out.println("Thread 2: Waiting for lock1");

                synchronized (lock1){
                    System.out.println("Thread 2: Has lock2 and lock1");
                }
                System.out.println("Thread 2: Released lock1");
            }
            System.out.println("Thread 2: Released lock2. Exiting...");
        }

        // BELOW IS THE SOLUTION 1 --> Obtaining the lock in the same order
//        @Override
//        public void run(){
//            synchronized (lock1){
//                System.out.println("Thread 2: Has lock1");
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("Thread 2: Waiting for lock2");
//
//                synchronized (lock2){
//                    System.out.println("Thread 2: Has lock1 and lock2");
//                }
//                System.out.println("Thread 2: Released lock2");
//            }
//            System.out.println("Thread 2: Released lock1. Exiting...");
//        }

    }

}
