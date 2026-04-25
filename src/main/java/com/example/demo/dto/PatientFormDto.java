package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

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

    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }

    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate v) { this.dob = v; }

    public String getGender() { return gender; }
    public void setGender(String v) { this.gender = v; }

    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }

    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }

    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }
}
