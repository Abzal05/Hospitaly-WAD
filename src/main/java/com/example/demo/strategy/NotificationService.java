package com.example.demo.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Holds all NotificationStrategy implementations and delegates
 * to the one selected by app.notification.strategy (default: console).
 *
 * Demonstrates the Strategy Pattern: the active algorithm is chosen
 * at runtime via configuration, with no changes to calling code.
 */
@Service
public class NotificationService {

    private final Map<String, NotificationStrategy> strategyMap;
    private final String activeStrategyName;

    public NotificationService(List<NotificationStrategy> strategies,
                                @Value("${app.notification.strategy:console}") String activeStrategyName) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(NotificationStrategy::getName, Function.identity()));
        this.activeStrategyName = activeStrategyName;

        System.out.println("[NotificationService] Active strategy: " + activeStrategyName
                + " | Available: " + this.strategyMap.keySet());
    }

    /** Returns the currently active strategy (fallback to first available). */
    public NotificationStrategy active() {
        return strategyMap.getOrDefault(activeStrategyName,
                strategyMap.values().iterator().next());
    }

    public void sendWelcome(String email, String username) {
        String recipient = (email != null && !email.isBlank()) ? email : username;
        active().send(recipient,
                "Добро пожаловать в систему!",
                "Уважаемый " + username + ", ваш аккаунт успешно создан.");
    }

    public void sendAppointmentConfirmed(String email, String patientName, String details) {
        String recipient = (email != null && !email.isBlank()) ? email : patientName;
        active().send(recipient,
                "Запись подтверждена",
                "Уважаемый(ая) " + patientName + ", ваша запись на приём подтверждена: " + details);
    }

    public void sendAppointmentCancelled(String email, String patientName, String details) {
        String recipient = (email != null && !email.isBlank()) ? email : patientName;
        active().send(recipient,
                "Запись отменена",
                "Уважаемый(ая) " + patientName + ", ваша запись на приём отменена: " + details);
    }
}
