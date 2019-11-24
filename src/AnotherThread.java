public class AnotherThread extends Thread{

    @Override
    public void run() {
        System.out.println(ThreadColour.ANSI_RED + "hello from " + currentThread().getName());

        try {
            Thread.sleep(3000); // 3 seconds
        }
        catch (InterruptedException e){
            System.out.println(ThreadColour.ANSI_RED + "I was interrupted by another unknown Thread which caused " + e);
            return;
        }

        System.out.println(ThreadColour.ANSI_RED + "3 Seconds has passed");

    }
}
