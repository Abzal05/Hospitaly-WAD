package com.example.demo.strategy;

/**
 * Strategy Pattern — defines a family of notification algorithms
 * that can be swapped at runtime via app.notification.strategy config.
 */
public interface NotificationStrategy {

    /** Unique name used for strategy selection (e.g. "console", "email"). */
    String getName();

    void send(String recipient, String subject, String body);
}
