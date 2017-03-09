package ru.naumen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Example_8_coordination {

    static class Car {
        private boolean waxOn = false;
        public synchronized void waxed() {
            waxOn = true; // Готово к обработке
            notifyAll();
        }
        public synchronized void buffed() {
            waxOn = false; // Готово к нанесению очередного слоя
            notifyAll();
        }
        public synchronized void waitForWaxing()
                throws InterruptedException {
            while(waxOn == false)
                wait();
        }
        public synchronized void waitForBuffing()
                throws InterruptedException {
            while(waxOn == true)
                wait();
        }
    }

    static class WaxOn implements Runnable {
        private Car car;
        public WaxOn(Car c) { car = c; }
        public void run() {
            try {
                while(!Thread.interrupted()) {
                    print("Wax On! ");
                    TimeUnit.MILLISECONDS.sleep(200);
                    car.waxed();
                    car.waitForBuffing();
                }
            } catch(InterruptedException e) {
                print("Exiting via interrupt");
            }
            print("Ending Wax On task");
        }
    }

    static class WaxOff implements Runnable {
        private Car car;
        public WaxOff(Car c) { car = c; }
        public void run() {
            try {
                while(!Thread.interrupted()) {
                    car.waitForWaxing();
                    print("Wax Off! ");
                    TimeUnit.MILLISECONDS.sleep(200);
                    car.buffed();
                }
            } catch(InterruptedException e) {
                print("Exiting via interrupt");
            }
            print("Ending Wax Off task");
        }
    }

    private static void print(String msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) throws Exception {
        Car car = new Car();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new WaxOff(car));
        exec.execute(new WaxOn(car));
        TimeUnit.SECONDS.sleep(5); // Небольшая задержка...
        exec.shutdownNow(); // Прерывание всех задач
    }

}
