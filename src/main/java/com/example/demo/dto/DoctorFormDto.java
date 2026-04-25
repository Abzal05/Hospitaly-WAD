package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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

    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }

    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String v) { this.specialty = v; }

    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }

    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }

    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long v) { this.hospitalId = v; }
}
