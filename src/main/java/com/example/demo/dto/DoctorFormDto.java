package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DoctorFormDto {

    @NotBlank(message = "Имя обязательно")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "Специальность обязательна")
    @Size(max = 100)
    private String specialty;

    @Email(message = "Некорректный email")
    private String email;

    @Size(max = 30)
    private String phone;

    private Long hospitalId;
}
