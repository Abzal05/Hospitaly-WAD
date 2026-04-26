package com.example.demo.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAppointmentRequest {

    @NotNull(message = "Пациент обязателен")
    private Long patientId;

    @NotNull(message = "Врач обязателен")
    private Long doctorId;

    @NotNull(message = "Больница обязательна")
    private Long hospitalId;

    @NotNull(message = "Дата и время обязательны")
    @Future(message = "Дата приёма должна быть в будущем")
    private LocalDateTime appointmentDateTime;

    @NotBlank(message = "Причина визита обязательна")
    private String reason;
}
