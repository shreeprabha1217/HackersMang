package com.hackersmang.aidemo.race;

import org.springframework.web.bind.annotation.*;

/**
 * Lets you trigger the race condition over HTTP during the talk, e.g. with
 * a quick shell loop firing concurrent curl requests, if you'd rather demo
 * it that way than with the JUnit harness in CounterConcurrencyTest.
 */
@RestController
@RequestMapping("/race")
public class CounterController {

    private final CounterService counterService;

    public CounterController(CounterService counterService) {
        this.counterService = counterService;
    }

    @PostMapping("/increment")
    public int increment() {
        counterService.increment();
        return counterService.getCount();
    }

    @PostMapping("/reset")
    public int reset() {
        counterService.reset();
        return counterService.getCount();
    }

    @GetMapping("/count")
    public int count() {
        return counterService.getCount();
    }
}
