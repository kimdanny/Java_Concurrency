package Thread_and_Runnable;

public class Main {

    public static void main(String args[]){

        System.out.println(ThreadColour.ANSI_GREEN + "hello from the main Thread");

        AnotherThread anotherThread = new AnotherThread();
        anotherThread.setName("<Another Thread>");
        anotherThread.start();


        new Thread(){
            @Override
            public void run() {
                System.out.println(ThreadColour.ANSI_BLUE + "hello from the anonymous Class Thread");
            }
        }.start();

        Thread myRunnable = new Thread(new MyRunnable(){
            @Override
            public void run() {
                System.out.println(ThreadColour.ANSI_CYAN + "From anonymous class's implementation of run()");
                try{
                    anotherThread.join();  // can also do join(2000)
                    System.out.println(ThreadColour.ANSI_CYAN + "anotherThread has terminated so i'm running");
                }
                catch (InterruptedException e){
                    System.out.println(ThreadColour.ANSI_CYAN + "I was interrupted");
                }
            }
        });
        myRunnable.start();

        System.out.println(ThreadColour.ANSI_GREEN + "hello again from the main Thread");

        // the order of execution is not always the same

        // anotherThread.start();
        /*
        This gives you Exception in thread "main" java.lang.IllegalThreadStateException.
        Can't Reuse the same instance
        * */

    }
}
