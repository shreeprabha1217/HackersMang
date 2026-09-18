package com.hackersmang.aidemo.silentfailure;

public interface EmailGateway {
    /** Throws EmailDeliveryException if the send fails. */
    void send(String toAddress, String message);
}
