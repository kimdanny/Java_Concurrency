package MultiThreading;

import Thread_and_Runnable.ThreadColour;

public class Main {

    public static void main(String args[]){
        Countdown countdown = new Countdown();

        CountdownThread t1 = new CountdownThread(countdown);
        t1.setName("Thread 1");
        CountdownThread t2 = new CountdownThread(countdown);
        t2.setName("Thread 2");

        t1.start();
        t2.start();

    }
}


class Countdown{
    private int i;

    public void doCountdown(){
        String colour;

        switch (Thread.currentThread().getName()){
            case "Thread 1":
                colour = ThreadColour.ANSI_CYAN;
                break;
            case "Thread 2":
                colour = ThreadColour.ANSI_PURPLE;
                break;
            default:
                colour = ThreadColour.ANSI_BLUE;
        }
        /*
        Threads share code and heap space memory.
        However, each Thread has their own copy of
            1. Program Counter
            2. Stack memory space for procedure call parameters and local variables
            3. Other registers for local variables

        Note that in the main method in the Main class,
        Two Threads are sharing one object, countdown.

        When JVM allocate space for local variable like int i, it uses each Thread's stack.
        Therefore, with the for loop below, Each Thread will countdown from 10 to 1.

        However, once you make a instance variable as private int i; and then use that in the for loop,
        Thread 1 and 2 will countdown together, sharing their instance variable i, which is allocated in the heap memory.

        One way to solve this problem is not to share the same object. Simple.
        However, in real world, there are many cases where you must share the same object.
        For example in bank, it does not make sense to make multiple bank account objects.
        So in order to keep the integrity of the data intact, you must use only one object. You can solve this problem with synchronization.

        1. public synchronized void doCountdown()
        2. synchronized block -> synchronize using an object
            The only thing that should be synchronized here is the for loop
              2-1. Do we synchronize local variable? like colour?
                    => Local variables are stored in each stack.
                        Each Thread will have their own copy of the local variable and each copy is an object that has its own lock.
                        When we want to synchronize using an object, we have to use an object that threads will share
                        so that they are both competing for the same lock. so using local variable as the object won't work.

                        When using a local object variable the object references are stored in thread stack but object values are stored on the heap.
                        Therefore, the object references will then be different in each threads, while they have same object values on the shared heap.

              2-2. synchronize with shared object
                   => Here, synchronize using the Countdown object, which is 'this'

         3. can synchronize static methods or static objects
            => when you do this, the lock that use is owned by the class object associated with the object class.

        */

        synchronized (this) {
            for (i = 10; i > 0; i--) {
                System.out.println(colour + Thread.currentThread().getName() + ": i = " + i);
            }
        }

    }

}

class CountdownThread extends Thread{
    private Countdown threadCountdown;

    public CountdownThread(Countdown countdown){
        threadCountdown = countdown;
    }

    @Override
    public void run() {
        threadCountdown.doCountdown();
    }
}