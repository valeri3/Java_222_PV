package learning.itstep.java_222_pv.newpackage;

import java.util.Random;
import java.util.concurrent.*;
import java.util.function.*;

public class Point {
    private final int x;
    private final int y;

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    private long startTime;
    private final Random random = new Random();

    public void demo() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        startTime = System.nanoTime();

        Future<?> futureTask = CompletableFuture
                .supplyAsync(coordinatesSupplier, executor)
                .thenApply(pointCreator)
                .thenApply(positionAnalyzer)
                .thenAccept(resultPrinter);

        try {
            futureTask.get();
            executor.shutdown();
            executor.awaitTermination(3, TimeUnit.SECONDS);
            executor.shutdownNow();
        } catch (Exception e) {
            log("Error: " + e.getMessage());
        }
    }

    private final Supplier<int[]> coordinatesSupplier = () -> {
        log("Generating random coordinates...");
        pause();
        int xVal = random.nextInt(-10, 11);
        int yVal = random.nextInt(-10, 11);
        log("Coordinates generated: [" + xVal + ", " + yVal + "]");
        return new int[]{xVal, yVal};
    };

    private final Function<int[], Point> pointCreator = coords -> {
        log("Creating point from coordinates [" + coords[0] + ", " + coords[1] + "]");
        pause();
        Point point = new Point(coords[0], coords[1]);
        log("Point created: " + point);
        return point;
    };

    private final Function<Point, String> positionAnalyzer = point -> {
        log("Analyzing point position " + point);
        pause();

        int px = point.getX();
        int py = point.getY();
        String analysis;

        if (px == 0 && py == 0) {
            analysis = point + " → origin";
        } else if (px == 0) {
            analysis = point + " → lies on Y axis";
        } else if (py == 0) {
            analysis = point + " → lies on X axis";
        } else if (px > 0 && py > 0) {
            analysis = point + " → first quadrant";
        } else if (px < 0 && py > 0) {
            analysis = point + " → second quadrant";
        } else if (px < 0 && py < 0) {
            analysis = point + " → third quadrant";
        } else {
            analysis = point + " → fourth quadrant";
        }

        log("Analysis result: " + analysis);
        return analysis;
    };

    private final Consumer<String> resultPrinter = output -> {
        log("Printing result...");
        pause();
        log("Done: " + output);
    };

    private void log(String message) {
        System.out.printf("%.1f ms | %s%n", elapsedMs(), message);
    }

    private double elapsedMs() {
        return (System.nanoTime() - startTime) / 1e6;
    }

    private void pause() {
        try {
            Thread.sleep(random.nextInt(123, 555));
        } catch (InterruptedException ex) {
            log("Thread interrupted: " + ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
