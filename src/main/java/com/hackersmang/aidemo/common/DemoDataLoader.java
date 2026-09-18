package com.hackersmang.aidemo.common;

import com.hackersmang.aidemo.nplusone.Order;
import com.hackersmang.aidemo.nplusone.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds 200 orders with 3 items each on startup, purely for the N+1 demo.
 * With this little data the buggy /nplusone/revenue endpoint still returns
 * the correct number — it just does it in 201 queries instead of 1. Watch
 * the console, not the response, to see the bug.
 */
@Component
public class DemoDataLoader implements CommandLineRunner {

    private final OrderRepository orderRepository;

    public DemoDataLoader(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        for (int i = 1; i <= 200; i++) {
            Order order = new Order("Customer " + i);
            order.addItem("Widget", 19.99);
            order.addItem("Gadget", 49.50);
            order.addItem("Gizmo", 9.75);
            orderRepository.save(order);
        }
        System.out.println(">> Seeded 200 orders with 3 items each for the N+1 demo.");
    }
}
