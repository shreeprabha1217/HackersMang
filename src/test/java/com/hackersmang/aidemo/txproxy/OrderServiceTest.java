package com.hackersmang.aidemo.txproxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The happy-path test that ships and stays green. It never exercises the
 * failure branch, so the missing rollback has no way to show up here.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository repository;

    @Test
    void placesOrderSuccessfully() {
        orderService.placeOrder(new Order("Asha", 250));

        assertEquals(1, repository.count()); // PASSES
    }
}
