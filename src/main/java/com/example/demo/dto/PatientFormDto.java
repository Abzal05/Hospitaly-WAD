package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientFormDto {

    @NotBlank(message = "Имя обязательно")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(max = 100)
    private String lastName;

    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate dob;

    private String gender;

    @Email(message = "Некорректный email")
    private String email;

    @Size(max = 30)
    private String phone;

    @Size(max = 255)
    private String address;
}
