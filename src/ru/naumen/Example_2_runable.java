package ru.naumen;

import java.util.concurrent.*;

public class Example_2_runable {

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

    //: concurrency/CachedThreadPool.java
    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        // ExecutorService exec = Executors.newFixedThreadPool(5);
        for(int i = 0; i < 5; i++)
            exec.execute(new LiftOff());
        exec.shutdown();
    }

}
