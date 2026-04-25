package com.example.demo.model;

public enum AppointmentStatus {
    PENDING,
    SCHEDULED,
    COMPLETED,
    CANCELLED,
    FAILED;

    public boolean canTransitionTo(AppointmentStatus next) {
        return switch (this) {
            case PENDING   -> next == SCHEDULED || next == CANCELLED || next == FAILED;
            case SCHEDULED -> next == COMPLETED || next == CANCELLED;
            default        -> false;
        };
    }
}
