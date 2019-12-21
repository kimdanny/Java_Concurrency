package ProducerConsumer2;

import Thread_and_Runnable.ThreadColour;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ProducerConsumer2.Main.EOF;

public class Main {
    public static final String EOF = "EOF";

    public static void main(String args[]){
        List<String> buffer = new ArrayList<>();
        MyProducer myProducer = new MyProducer(buffer, ThreadColour.ANSI_CYAN);
        MyConsumer myConsumer1 = new MyConsumer(buffer, ThreadColour.ANSI_RED);
        MyConsumer myConsumer2 = new MyConsumer(buffer, ThreadColour.ANSI_BLUE);

        new Thread(myProducer).start();
        new Thread(myConsumer1).start();
        new Thread(myConsumer2).start();

    }
}

class MyProducer implements Runnable{
    private List<String> buffer;
    private String color;

    public MyProducer(List<String> buffer, String color){
        this.buffer = buffer;
        this.color = color;
    }

    public void run(){
        Random random = new Random();
        String nums[] = {"1", "2", "3", "4", "5"};

        for (String num : nums){
            try {
                System.out.println(color + "Adding..." + num);
                buffer.add(num);
                Thread.sleep(random.nextInt(1000));
            }
            catch (InterruptedException e){
                System.out.println("Producer was interrupted by " + e);
            }
        }
        System.out.println(color + "Adding EOF and exiting");
        buffer.add("EOF");

    }

}


class MyConsumer implements Runnable{
    private List<String> buffer;
    private String color;

    public MyConsumer(List<String> buffer, String color){
        this.buffer = buffer;
        this.color = color;
    }

    public void run(){
        while(true){
            if (buffer.isEmpty()){
                continue;
            }
            if (buffer.get(0).equals(EOF)){
                System.out.println(color + "Exiting");
                break;
            }
            else {
                System.out.println(color + "Removed" + buffer.get(0));
            }
        }

    }

}
















