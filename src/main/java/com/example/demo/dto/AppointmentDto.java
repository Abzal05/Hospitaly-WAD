package com.example.demo.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class AppointmentDto {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long hospitalId;
    private String patientName;
    private String patientEmail;
    private String doctorName;
    private String hospitalName;
    private LocalDateTime appointmentDateTime;
    private String status;
    private String reason;
    private Instant createdAt;
}
