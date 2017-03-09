package ru.naumen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.*;

public class Example_6_locks {

    private static void print(String msg) {
        System.out.println(msg);
    }

    // Предотвращение потоковых конфликтов с использованием мьютексов.

    public static abstract class IntGenerator {
        private volatile boolean canceled = false;
        public abstract int next();
        // Allow this to be canceled:
        public void cancel() { canceled = true; }
        public boolean isCanceled() { return canceled; }
    }

    public static class EvenChecker implements Runnable {
        private IntGenerator generator;
        private final int id;
        public EvenChecker(IntGenerator g, int ident) {
            generator = g;
            id = ident;
        }
        public void run() {
            while(!generator.isCanceled()) {
                int val = generator.next();
                if(val % 2 != 0) {
                    System.out.println(val + " not even!");
                    generator.cancel(); // Отмена всех EvenChecker
                }
            }
        }
        // Тестирование произвольного типа IntGenerator:
        public static void test(IntGenerator gp, int count) {
            System.out.println("Press Control-C to exit");
            ExecutorService exec = Executors.newCachedThreadPool();
            for(int i = 0; i < count; i++)
                exec.execute(new EvenChecker(gp, i));
            exec.shutdown();
        }
        // Значение по умолчанию для count:
        public static void test(IntGenerator gp) {
            test(gp, 10);
        }
    }

    public static class EvenGenerator extends IntGenerator {
        private int currentEvenValue = 0;
        public int next() {
            ++currentEvenValue;  // Опасная точка!
            ++currentEvenValue;
            return currentEvenValue;
        }
    }

    public static class SynchronizedEvenGenerator extends IntGenerator {
        private int currentEvenValue = 0;
        public synchronized int next() {
            ++currentEvenValue;
            Thread.yield(); // Ускоряем сбой
            ++currentEvenValue;
            return currentEvenValue;
        }
    }

    public static class MutexEvenGenerator extends IntGenerator {
        private int currentEvenValue = 0;
        private Lock lock = new ReentrantLock();
        public int next() {
            lock.lock();
            try {
                ++currentEvenValue;
                Thread.yield(); // Ускоряем сбой
                ++currentEvenValue;
                return currentEvenValue;
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        EvenChecker.test(new EvenGenerator());
//        EvenChecker.test(new SynchronizedEvenGenerator());
//        EvenChecker.test(new MutexEvenGenerator());
    }
}
