package com.hackersmang.aidemo.race;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * THIS is the test that breaks. Run it on its own during the talk:
 *
 *   mvn -Dtest=CounterConcurrencyTest test
 *
 * 10 threads x 1000 increments each should leave the counter at 10,000.
 * With the buggy int version it almost always comes up short — run it a
 * couple of times if the first run happens to get lucky (that's a live,
 * honest demonstration of why "it passed on my machine" isn't proof of
 * thread safety). Swap CounterService over to the AtomicInteger version
 * and re-run to show it passing reliably.
 */
class CounterConcurrencyTest {

    @Test
    void incrementsConcurrently() throws InterruptedException {
        CounterService counter = new CounterService();
        int threads = 10;
        int incrementsPerThread = 1000;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                for (int i = 0; i < incrementsPerThread; i++) {
                    counter.increment();
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("Expected: " + (threads * incrementsPerThread));
        System.out.println("Actual:   " + counter.getCount());

        // FAILS with the buggy int — lost updates under concurrent load.
        // PASSES once CounterService uses AtomicInteger.
        assertEquals(threads * incrementsPerThread, counter.getCount());
    }
}
