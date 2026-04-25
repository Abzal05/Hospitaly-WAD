package com.example.demo.event;

import com.example.demo.dto.AppointmentDto;
import org.springframework.context.ApplicationEvent;

/**
 * Observer Pattern — domain event fired when an appointment is created.
 */
public class AppointmentCreatedEvent extends ApplicationEvent {

    private final AppointmentDto appointment;

    public AppointmentCreatedEvent(Object source, AppointmentDto appointment) {
        super(source);
        this.appointment = appointment;
    }

    public AppointmentDto getAppointment() {
        return appointment;
    }
}
