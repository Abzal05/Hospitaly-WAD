package com.example.demo.service;

import com.example.demo.dto.AppointmentDto;
import com.example.demo.dto.CreateAppointmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    AppointmentDto create(CreateAppointmentRequest request);
    Page<AppointmentDto> findAll(Pageable pageable);
    void cancel(Long id);
}

