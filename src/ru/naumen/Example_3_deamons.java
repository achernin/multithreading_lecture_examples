package ru.naumen;

import java.util.concurrent.*;

public class Example_3_deamons {

    private static void print(String msg) {
        System.out.println(msg);
    }

    public static class SimpleDaemons implements Runnable {
        public void run() {
            try {
                while(true) {
                    TimeUnit.MILLISECONDS.sleep(100);
                    print(Thread.currentThread() + " " + this);
                }
            } catch(InterruptedException e) {
                print("sleep() interrupted");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        for(int i = 0; i < 10; i++) {
            Thread daemon = new Thread(new SimpleDaemons());
            daemon.setDaemon(true); // Необходимо вызвать перед start()
            daemon.start();
        }
        print("All daemons started");
        TimeUnit.MILLISECONDS.sleep(175);
    }


}
