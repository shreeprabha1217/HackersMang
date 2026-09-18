package com.hackersmang.aidemo.silentfailure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The "happy path" test every reviewer would expect to see, and the one
 * that ships. It passes. It always will — nothing about this bug shows up
 * in a return-value assertion, because the return value is the thing
 * that's lying.
 */
class NotificationServiceTest {

    @Test
    void reportsSuccessForAWorkingAddress() {
        FakeEmailGateway gateway = new FakeEmailGateway();
        NotificationService service = new NotificationService(gateway);

        boolean sent = service.sendOrderConfirmation("asha@example.com", "A100");

        assertTrue(sent);
        assertEquals(1, gateway.getSentEmails().size()); // PASSES — it really was sent
    }

    /**
     * THIS is the one to run live. The method call succeeds — no exception
     * reaches the test, exactly as if everything worked. Only checking the
     * gateway's own record reveals nothing was actually delivered.
     */
    @Test
    void reportsSuccessEvenWhenDeliveryFails() {
        FakeEmailGateway gateway = new FakeEmailGateway();
        NotificationService service = new NotificationService(gateway);

        boolean sent = service.sendOrderConfirmation("ravi@bounce.test", "A101");

        assertTrue(sent, "The method claims success..."); // PASSES — that's the trap
        assertEquals(0, gateway.getSentEmails().size(),
                "...but nothing was actually sent — the failure was swallowed silently");
        // The second assertion PASSES too — 0 emails really did go out. Both
        // assertions being true at once, for the same call, is the bug.
    }
}
