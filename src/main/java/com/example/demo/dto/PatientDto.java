package com.example.demo.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class PatientDto {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private Instant createdAt;
}
