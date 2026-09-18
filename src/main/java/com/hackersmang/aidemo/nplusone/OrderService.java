package com.hackersmang.aidemo.nplusone;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DEMO 3: The N+1 query problem.
 *
 * totalRevenue() looks completely ordinary: fetch the orders, sum up their
 * items. It compiles. It returns the right number. A unit test asserting
 * on the total will pass — N+1 is a performance bug, not a correctness
 * bug, so no assertion on the RESULT will ever catch it.
 *
 * What it actually does: 1 query for findAll(), then one MORE query per
 * order the first time .getItems() is touched inside the loop, because
 * the association is lazy. 50 orders means 51 queries. 200 orders means
 * 201. It gets worse in direct proportion to the data, which is exactly
 * why it's invisible with 3 rows of test data and brutal in production.
 */
@Service("npOrderService")
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    // --- BUGGY: 1 query to fetch orders, then N more lazy-loading items ---
    @Transactional(readOnly = true)
    public double totalRevenueBuggy() {
        double total = 0;
        for (Order order : repository.findAll()) {
            for (OrderItem item : order.getItems()) { // triggers a query, per order
                total += item.getPrice();
            }
        }
        return total;
    }

    // --- FIXED: one query, via JOIN FETCH ---
    @Transactional(readOnly = true)
    public double totalRevenueFixed() {
        double total = 0;
        for (Order order : repository.findAllWithItems()) {
            for (OrderItem item : order.getItems()) { // already loaded, no extra query
                total += item.getPrice();
            }
        }
        return total;
    }
}
