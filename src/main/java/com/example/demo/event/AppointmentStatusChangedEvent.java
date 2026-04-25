package com.example.demo.event;

import com.example.demo.model.AppointmentStatus;
import org.springframework.context.ApplicationEvent;

/**
 * Observer Pattern — domain event fired when an appointment changes status.
 */
public class AppointmentStatusChangedEvent extends ApplicationEvent {

    private final Long appointmentId;
    private final String patientEmail;
    private final String patientName;
    private final AppointmentStatus previousStatus;
    private final AppointmentStatus newStatus;

    public AppointmentStatusChangedEvent(Object source,
                                          Long appointmentId,
                                          String patientEmail,
                                          String patientName,
                                          AppointmentStatus previousStatus,
                                          AppointmentStatus newStatus) {
        super(source);
        this.appointmentId = appointmentId;
        this.patientEmail = patientEmail;
        this.patientName = patientName;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
    }

    public Long getAppointmentId() { return appointmentId; }
    public String getPatientEmail() { return patientEmail; }
    public String getPatientName() { return patientName; }
    public AppointmentStatus getPreviousStatus() { return previousStatus; }
    public AppointmentStatus getNewStatus() { return newStatus; }
}
