package com.hackersmang.aidemo.silentfailure;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Stands in for a real email provider (SES, SendGrid, ...) for the demo.
 * Throws for any address at "bounce.test" — that's your "the provider is
 * down / the address is bad" scenario to trigger live. Every address that
 * DOES go through is recorded in sentEmails, so a test or the controller
 * can prove whether a message actually went out, independent of whatever
 * NotificationService claims happened.
 */
@Component
public class FakeEmailGateway implements EmailGateway {

    private final List<String> sentEmails = new ArrayList<>();

    @Override
    public void send(String toAddress, String message) {
        if (toAddress.endsWith("@bounce.test")) {
            throw new EmailDeliveryException("Provider rejected address: " + toAddress);
        }
        sentEmails.add(toAddress);
    }

    public List<String> getSentEmails() {
        return sentEmails;
    }
}
