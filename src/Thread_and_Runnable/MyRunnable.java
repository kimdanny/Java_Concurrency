package Thread_and_Runnable;

public class MyRunnable implements Runnable {


    @Override
    public void run() {
        System.out.println(ThreadColour.ANSI_CYAN + "This is from MyRunnable's implementation of run()");
    }
}
