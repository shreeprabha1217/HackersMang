package com.hackersmang.aidemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the "Can AI Catch Bugs Your Compiler Can't?" demo repo.
 *
 * Four independent bug demos live under this application, each in its own
 * package so you can present them one at a time without the others getting
 * in the way:
 *
 *   race          - a race condition an atomic type fixes
 *   txproxy       - @Transactional silently skipped via self-invocation
 *   nplusone      - a JPA lazy-loading N+1 query explosion
 *   silentfailure - an exception that is caught and thrown away, so the
 *                   caller sees success while the real work never happened
 *
 * Run with: mvn spring-boot:run
 */
@SpringBootApplication
public class AiDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiDemoApplication.class, args);
    }
}
