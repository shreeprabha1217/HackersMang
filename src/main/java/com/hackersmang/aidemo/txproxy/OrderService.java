package com.hackersmang.aidemo.txproxy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DEMO 2: @Transactional silently bypassing its own proxy.
 *
 * saveOrder() is annotated @Transactional because it's meant to be
 * all-or-nothing: if the payment charge fails, the order that was just
 * saved should be rolled back with it.
 *
 * It never rolls back. Spring implements @Transactional with a dynamic
 * proxy that wraps this bean; the transactional advice only runs when the
 * proxy's saveOrder() is invoked from OUTSIDE the class (e.g. by the
 * controller). Here, placeOrder() calls `this.saveOrder(order)` — a
 * self-invocation that calls the real method directly, never touching the
 * proxy. @Transactional is parsed, compiled, and completely inert.
 *
 * There's no exception for this, no startup warning, no log line. The
 * order silently stays committed even when the method around it "fails."
 */
@Service("txOrderService")
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    // --- Public entry point, called by OrderController ---
    public void placeOrder(Order order) {
        validate(order);
        this.saveOrder(order); // BUG: self-invocation skips the Spring proxy
    }

    /**
     * Looks atomic. Isn't, because of how it's called above.
     */
    @Transactional
    public void saveOrder(Order order) {
        repository.save(order);
        chargeCustomer(order); // may throw — should roll back the save, but won't
    }

    private void validate(Order order) {
        if (order.getAmount() <= 0) {
            throw new IllegalArgumentException("Order amount must be positive");
        }
    }

    /** Simulates a payment gateway that declines large charges. */
    private void chargeCustomer(Order order) {
        if (order.getAmount() > 1000) {
            throw new PaymentDeclinedException(
                    "Payment gateway declined the charge of " + order.getAmount()
                            + " for " + order.getCustomerName());
        }
    }

    // --- FIXED: one way to fix it — stop self-invoking, always go through
    // the bean the container gave you. Delete placeOrder() above and
    // instead have OrderController call saveOrder(...) directly, or split
    // this class into two beans (a "gateway" that isn't transactional, and
    // a transactional "writer") so the annotated method is always reached
    // through Spring's proxy. See README.md for the full writeup. ---
}
