package ru.naumen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Example_7_notify {

    public static class Message {
        // поле, с которым будут работать потоки через вызовы геттеров и сеттеров
        private String msg;

        public Message(String str){
            this.msg=str;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String str) {
            this.msg=str;
        }

    }

    public static class Waiter implements Runnable{

        private Message msg;

        public Waiter(Message m){
            this.msg = m;
        }

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            synchronized (msg) {
                try{
                    System.out.println(name + " ждем вызов метода notify: " + System.currentTimeMillis());
                    msg.wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                System.out.println(name + " был вызов метода notify: " + System.currentTimeMillis());
                // обработаем наше сообщение
                System.out.println(name + " : " + msg.getMsg());
            }
        }
    }

    public static class Notifier implements Runnable {

        private Message msg;

        public Notifier(Message msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            System.out.println(name + " стартовал");
            try {
                Thread.sleep(1000);
                synchronized (msg) {
                    msg.setMsg(name + " поток Notifier отработал");
                    msg.notify();
                    //todo msg.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Message msg = new Message("обработать");
        Waiter waiter = new Waiter(msg);
        new Thread(waiter,"waiter").start();

        Waiter waiter1 = new Waiter(msg);
        new Thread(waiter1, "waiter1").start();

        Notifier notifier = new Notifier(msg);
        new Thread(notifier, "notifier").start();
        System.out.println("Стартовали все потоки");
    }
}
