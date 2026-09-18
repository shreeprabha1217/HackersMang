package com.hackersmang.aidemo.txproxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * THIS is the test to run live:
 *
 *   mvn -Dtest=OrderServiceRollbackTest test
 *
 * An order over 1000 fails payment. saveOrder() is @Transactional, so the
 * save should roll back along with the failed payment — repository.count()
 * should end at 0. It doesn't, because @Transactional never activates
 * (see OrderService for why). Fix the self-invocation and re-run to watch
 * this go green.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderServiceRollbackTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository repository;

    @Test
    void failedPaymentShouldRollBackTheOrder() {
        Order order = new Order("Ravi", 5000);

        assertThrows(PaymentDeclinedException.class, () -> orderService.placeOrder(order));

        // FAILS today: the order is still there despite the "rolled back" transaction.
        // PASSES once the self-invocation bug is fixed.
        assertEquals(0, repository.count(),
                "Expected the failed order to be rolled back, but it's still in the database");
    }
}
