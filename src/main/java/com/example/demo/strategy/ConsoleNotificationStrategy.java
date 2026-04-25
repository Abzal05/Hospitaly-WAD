package com.example.demo.strategy;

import org.springframework.stereotype.Component;

/**
 * Strategy: outputs notifications to the console/log.
 * Active by default (app.notification.strategy=console).
 */
@Component
public class ConsoleNotificationStrategy implements NotificationStrategy {

    @Override
    public String getName() {
        return "console";
    }

    @Override
    public void send(String recipient, String subject, String body) {
        System.out.printf("[NOTIFICATION][console] To: %-30s | %s | %s%n", recipient, subject, body);
    }
}
