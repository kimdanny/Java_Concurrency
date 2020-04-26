package Deadlock2;

/**
 * -- Deadlock case 2 --
 * -- 1 --
 * Thread 1 acquires the lock on the Danny object and enters the sayHello() method.
 * It prints to the console then suspends then
 *
 * -- 2 --
 * Thread 2 acquire the lock on the Taylor object and enters the sayHello() method.
 * It prints to the console then suspends.
 *
 * -- 3 --
 * Thread 1 runs again and wants to say hello back to the Taylor object.
 * It tries to call the sayHelloBack() method using the Taylor object that was passed into the sayHello() method
 * but Thread 2 is holding the Taylor lock, so Thread 1 suspends.
 *
 * -- 4 --
 * Thread 2 runs again and wants to say hello back to the Danny object.
 * It tries to call the sayHelloBack() method using the Danny object that was passed into the sayHello() method
 * but Thread 1 in this case is holding the Danny lock, so Thread 2 suspends.
 *
 * Deadlock occurred.
 *
 * Deadlock occurred because of the order in which threads are trying to acquire locks that's usually the underlying cause of a deadlock.
 * Therefore, when synchronizing code, always be on the lookout for places where this can arise.
 */
public class Main {

    public static void main(String[] args) {
        PolitePerson Danny = new PolitePerson("Danny");
        PolitePerson Taylor = new PolitePerson("Taylor");

        // Thread 1
        new Thread(new Runnable() {
            @Override
            public void run() {
                Danny.sayHello(Taylor);
            }
        }).start();

        // Thread 2
        new Thread(new Runnable() {
            @Override
            public void run() {
                Taylor.sayHello(Danny);
            }
        }).start();

    }

    static class PolitePerson {
        private final String name;

        public PolitePerson(String name) {
            this.name = name;
        }

        public String getName(){
            return name;
        }

        public synchronized void sayHello(PolitePerson politePerson) {
            System.out.format("%s: %s has said hello to me%n", this.name, politePerson.getName());
            politePerson.sayHelloBack(this);
        }

        public synchronized void sayHelloBack(PolitePerson politePerson) {
            System.out.format("%s: %s has said hello back to me%n", this.name, politePerson.getName());
        }

    }
}
