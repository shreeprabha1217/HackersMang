package com.hackersmang.aidemo.nplusone;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hit either endpoint with spring.jpa.show-sql=true in the console open
 * next to your browser — /revenue floods it with one query per order,
 * /revenue-fixed prints exactly one.
 */
@RestController
@RequestMapping("/nplusone")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/revenue")
    public double revenue() {
        return orderService.totalRevenueBuggy();
    }

    @GetMapping("/revenue-fixed")
    public double revenueFixed() {
        return orderService.totalRevenueFixed();
    }
}
