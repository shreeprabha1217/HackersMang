package com.hackersmang.aidemo.race;

import org.springframework.stereotype.Service;

/**
 * DEMO 1: Race condition.
 *
 * This class compiles fine and every single-threaded test on it passes.
 * The bug only shows up when increment() is called concurrently: two
 * threads can both read the same value of `count` before either one
 * writes back, so one of the increments is silently lost. No exception,
 * no log line, no stack trace — the counter just ends up lower than it
 * should be.
 *
 * To fix it, either:
 *   - Change `count` to an AtomicInteger and call incrementAndGet(), or
 *   - Add `synchronized` to increment(), or
 *   - Push the increment down to the database as an atomic
 *     `UPDATE counters SET value = value + 1 WHERE id = ?`.
 *
 * The AtomicInteger fix is commented out below — swap it in live to show
 * the "after" state.
 */
@Service
public class CounterService {

    // --- BUGGY: plain int, non-atomic read-modify-write ---
    private int count = 0;

    public void increment() {
        count = count + 1; // read, then write — not atomic
    }

    public int getCount() {
        return count;
    }

    public void reset() {
        count = 0;
    }

    // --- FIXED: uncomment this block and delete the block above to show the fix ---
    //
    // private final java.util.concurrent.atomic.AtomicInteger count =
    //         new java.util.concurrent.atomic.AtomicInteger(0);
    //
    // public void increment() {
    //     count.incrementAndGet();
    // }
    //
    // public int getCount() {
    //     return count.get();
    // }
    //
    // public void reset() {
    //     count.set(0);
    // }
}
