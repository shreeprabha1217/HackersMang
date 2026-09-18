package com.hackersmang.aidemo.txproxy;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("txOrderController")
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository repository;

    public OrderController(OrderService orderService, OrderRepository repository) {
        this.orderService = orderService;
        this.repository = repository;
    }

    /**
     * Try:
     *   POST /orders {"customerName":"Asha","amount":250}     -> succeeds
     *   POST /orders {"customerName":"Ravi","amount":5000}    -> 500 error,
     *       but the order is still in /orders afterwards. That's the bug.
     */
    @PostMapping
    public String placeOrder(@RequestBody OrderRequest request) {
        orderService.placeOrder(new Order(request.customerName(), request.amount()));
        return "Order placed for " + request.customerName();
    }

    @GetMapping
    public List<Order> listOrders() {
        return repository.findAll();
    }

    public record OrderRequest(String customerName, double amount) {
    }
}
