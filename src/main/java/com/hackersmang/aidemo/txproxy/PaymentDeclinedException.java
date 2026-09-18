package com.hackersmang.aidemo.txproxy;

/** Simulates a downstream payment gateway declining a charge. */
public class PaymentDeclinedException extends RuntimeException {
    public PaymentDeclinedException(String message) {
        super(message);
    }
}
