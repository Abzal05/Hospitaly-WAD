package com.example.demo.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class DoctorDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String specialty;
    private String email;
    private String phone;
    private Instant createdAt;
}
