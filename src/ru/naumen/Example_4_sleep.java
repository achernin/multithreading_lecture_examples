package ru.naumen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Example_4_sleep {

//: concurrency/LiftOff.java
// Реализация интерфейса Runnable.

    public static class LiftOff implements Runnable {
        protected int countDown = 10; // Значение по умолчанию
        private static int taskCount = 0;
        private final int id = taskCount++;
        public LiftOff() {}
        public LiftOff(int countDown) {
            this.countDown = countDown;
        }
        public String status() {
            return "#" + id + "(" +
                    (countDown > 0 ? countDown : "Liftoff!") + "), ";
        }
        public void run() {
            while(countDown-- > 0) {
                System.out.print(status());
                Thread.yield();
            }
        }
    }

    public static class SleepingTask extends LiftOff {
        public void run() {
            try {
                while(countDown-- > 0) {
                    System.out.print(status());
                    // Старый стиль.
                    // Thread.sleep(l00);
                    // Стиль Java SE5/6:
                    TimeUnit.MILLISECONDS.sleep(100);
                }
            } catch(InterruptedException e) {
                System.err.println("Interrupted");
            }
        }
    }


    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        for(int i = 0; i < 5; i++)
            exec.execute(new SleepingTask());
        exec.shutdown();
    }

}
