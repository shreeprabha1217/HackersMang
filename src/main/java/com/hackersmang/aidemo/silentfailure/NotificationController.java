package com.hackersmang.aidemo.silentfailure;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Try:
 *   POST /notify {"email":"asha@example.com","orderId":"A100"}     -> "sent: true"
 *   POST /notify {"email":"ravi@bounce.test","orderId":"A101"}     -> ALSO "sent: true"
 *
 * Same success response both times. Only GET /notify/sent-log tells you
 * the second email never actually left the building.
 */
@RestController
@RequestMapping("/notify")
public class NotificationController {

    private final NotificationService notificationService;
    private final FakeEmailGateway emailGateway;

    public NotificationController(NotificationService notificationService, FakeEmailGateway emailGateway) {
        this.notificationService = notificationService;
        this.emailGateway = emailGateway;
    }

    @PostMapping
    public String sendConfirmation(@RequestBody NotifyRequest request) {
        boolean sent = notificationService.sendOrderConfirmation(request.email(), request.orderId());
        return "sent: " + sent; // always true today — that's the bug
    }

    @GetMapping("/sent-log")
    public List<String> sentLog() {
        return emailGateway.getSentEmails(); // the ground truth the response above hides
    }

    public record NotifyRequest(String email, String orderId) {
    }
}
