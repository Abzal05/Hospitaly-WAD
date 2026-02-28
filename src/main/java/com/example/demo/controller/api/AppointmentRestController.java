package com.example.demo.controller.api;

import com.example.demo.dto.AppointmentDto;
import com.example.demo.dto.CreateAppointmentRequest;
import com.example.demo.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentRestController {

    private final AppointmentService appointmentService;

    public AppointmentRestController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public Page<AppointmentDto> list(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        return appointmentService.findAll(PageRequest.of(page, size));
    }

    @PostMapping
    public ResponseEntity<AppointmentDto> create(@Valid @RequestBody CreateAppointmentRequest request) {
        AppointmentDto dto = appointmentService.create(request);
        return ResponseEntity.status(201).body(dto);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        appointmentService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}

