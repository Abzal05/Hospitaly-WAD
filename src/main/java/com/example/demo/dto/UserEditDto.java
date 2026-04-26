package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserEditDto {

    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 50, message = "Логин: от 3 до 50 символов")
    private String username;

    @Email(message = "Некорректный адрес электронной почты")
    private String email;

    @Size(min = 4, message = "Пароль: минимум 4 символа")
    private String password;

    private boolean enabled = true;

    private Set<String> roleNames = new HashSet<>();
}
