package com.hackersmang.aidemo.race;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This is the test that ships in every PR and stays green.
 * Single thread, sequential calls — the race condition has no way to
 * show up here, which is exactly the point of the talk.
 */
class CounterServiceTest {

    @Test
    void incrementsSequentially() {
        CounterService counter = new CounterService();

        for (int i = 0; i < 1000; i++) {
            counter.increment();
        }

        assertEquals(1000, counter.getCount()); // PASSES every time
    }
}
