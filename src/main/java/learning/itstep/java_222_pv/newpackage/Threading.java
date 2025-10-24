package learning.itstep.java_222_pv.newpackage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class Threading {

    public void demo() {
        demoRandomString();
    }

    static class MonthPercent implements Callable<Double> {

        private final int month;

        MonthPercent(int month) {
            this.month = month;
        }

        @Override
        public Double call() throws Exception {
            Thread.sleep(500); // 
            return this.month / 10.0;
        }
    }

    private void demoPercent() {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        List<Future<Double>> tasks = new ArrayList<>();
        long t = System.nanoTime();
       
        for (int i = 0; i <= 12; i++) {
            tasks.add(threadPool.submit(new MonthPercent(i)));
        }

        try {
            Double sum = 100.0;
            for (Future<Double> task : tasks) {
                Double res = task.get();
                System.out.println(res);
                sum *= (1.0 + res / 100.0);
            }
            System.out.println("-----------------");
            System.out.printf("%.1f ms: %.3f\n", (System.nanoTime() - t) / 1e6, sum);
        } catch (InterruptedException | ExecutionException ex) {
            System.out.println(ex.getMessage());
        }

        threadPool.shutdown();
    }

    public void demo2() {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("Task 2 executed");
            }
        });

        Future<String> task2 = threadPool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("Task 2 - Callable executing");
                return "Task 2 - Callable executed";
            }
        });

        threadPool.shutdown();

        String res2;
        try {
            res2 = task2.get();
            System.out.println(res2);
        } catch (InterruptedException | ExecutionException ex) {
            System.out.println(ex.getMessage());
        }

    }

    private void demo1() {
        Runnable task1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Task 1 executed");
            }
        };

        new Thread(task1).start();
    }
    
    static class RandomCharTask implements Callable<Character> {
        private static final char[] ALPHABET 
                = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        @Override
        public Character call() throws Exception {
            Thread.sleep(ThreadLocalRandom.current().nextInt(2, 15));
            return ALPHABET[ThreadLocalRandom.current().nextInt(ALPHABET.length)];
        }
    }

    public String generateRandomString(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length must be >= 0");
        }
        if (length == 0) {
            return "";
        }
        int threads = Math.min(length, Runtime.getRuntime().availableProcessors());
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        try {
            List<Future<Character>> futures = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                futures.add(pool.submit(new RandomCharTask()));
            }

            char[] out = new char[length];
            for (int i = 0; i < length; i++) {
                try {
                    out[i] = futures.get(i).get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting char #" + i, e);
                } catch (ExecutionException e) {
                    throw new RuntimeException("Char task failed at index " + i, e.getCause());
                }
            }
            return new String(out);
        } finally {
            pool.shutdown();
        }
    }

    private void demoRandomString() {
        long t = System.nanoTime();
        String s = generateRandomString(24);
        System.out.printf("Generated in %.1f ms: %s%n", (System.nanoTime() - t) / 1e6, s);
    }
}
