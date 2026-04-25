package com.example.demo.event;

import com.example.demo.strategy.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Observer Pattern — listens to domain events and reacts asynchronously.
 * Decouples event producers (Services) from consumers (notifications, auditing).
 */
@Component
public class DomainEventListener {

    private final NotificationService notificationService;

    public DomainEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    @Async
    public void onUserRegistered(UserRegisteredEvent event) {
        String username = event.getUser().getUsername();
        String email    = event.getUser().getEmail();
        System.out.println("[EVENT] UserRegistered: " + username + " <" + email + ">");
        notificationService.sendWelcome(email, username);
    }

    @EventListener
    @Async
    public void onAppointmentCreated(AppointmentCreatedEvent event) {
        String details = String.format("врач: %s, дата: %s",
                event.getAppointment().getDoctorName(),
                event.getAppointment().getAppointmentDateTime());
        System.out.println("[EVENT] AppointmentCreated: id=" + event.getAppointment().getId());
        notificationService.sendAppointmentConfirmed(
                event.getAppointment().getPatientEmail(),
                event.getAppointment().getPatientName(),
                details);
    }

    @EventListener
    @Async
    public void onAppointmentStatusChanged(AppointmentStatusChangedEvent event) {
        System.out.printf("[EVENT] AppointmentStatusChanged: id=%d  %s → %s%n",
                event.getAppointmentId(),
                event.getPreviousStatus(),
                event.getNewStatus());

        if (event.getNewStatus().name().equals("CANCELLED")) {
            notificationService.sendAppointmentCancelled(
                    event.getPatientEmail(),
                    event.getPatientName(),
                    "запись #" + event.getAppointmentId() + " отменена");
        }
    }
}
