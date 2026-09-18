package com.hackersmang.aidemo.silentfailure;

import org.springframework.stereotype.Service;

/**
 * BONUS DEMO: silently broken with no exception ever reaching the caller.
 *
 * sendOrderConfirmation() compiles. It never throws — checked or
 * unchecked. Every caller sees a normal return and, if you only look at
 * the method signature or run the happy-path test, everything about it
 * looks completely fine. It ISN'T: when the email provider fails, the
 * catch block below throws the failure away and the method still reports
 * success. No log line, no metric, no retry, no dead-letter queue — the
 * customer just never gets their confirmation, and nothing in the system
 * ever records that it happened.
 *
 * This is deliberately the most common shape this bug takes in real
 * codebases: someone added a try/catch to stop one bad email address from
 * taking down checkout, and in doing so made every failure invisible.
 */
@Service
public class NotificationService {

    private final EmailGateway emailGateway;

    public NotificationService(EmailGateway emailGateway) {
        this.emailGateway = emailGateway;
    }

    // --- BUGGY: swallows the exception, always reports success ---
    public boolean sendOrderConfirmation(String toAddress, String orderId) {
        try {
            emailGateway.send(toAddress, "Your order " + orderId + " is confirmed!");
        } catch (Exception e) {
            // TODO: figure out why this happens sometimes
        }
        return true; // BUG: true whether or not the email above was ever sent
    }

    // --- FIXED: options, in increasing order of effort ---
    //
    // 1. Let it propagate and let the caller decide (simplest, often right):
    //
    //    public boolean sendOrderConfirmation(String toAddress, String orderId) {
    //        emailGateway.send(toAddress, "Your order " + orderId + " is confirmed!");
    //        return true;
    //    }
    //
    // 2. Report the real outcome instead of a fixed one:
    //
    //    public boolean sendOrderConfirmation(String toAddress, String orderId) {
    //        try {
    //            emailGateway.send(toAddress, "Your order " + orderId + " is confirmed!");
    //            return true;
    //        } catch (Exception e) {
    //            log.warn("Order confirmation email failed for {}: {}", orderId, e.getMessage());
    //            return false; // caller can retry, alert, or queue for later
    //        }
    //    }
}
