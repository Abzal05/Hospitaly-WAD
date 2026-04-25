package com.example.demo.service.impl;

import com.example.demo.dto.AppointmentDto;
import com.example.demo.dto.CreateAppointmentRequest;
import com.example.demo.event.AppointmentCreatedEvent;
import com.example.demo.event.AppointmentStatusChangedEvent;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.SystemStatsService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final ApplicationEventPublisher eventPublisher;   // Observer
    private final SystemStatsService statsService;            // Singleton

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                   PatientRepository patientRepository,
                                   DoctorRepository doctorRepository,
                                   HospitalRepository hospitalRepository,
                                   ApplicationEventPublisher eventPublisher,
                                   SystemStatsService statsService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
        this.eventPublisher = eventPublisher;
        this.statsService = statsService;
    }

    @Override
    public AppointmentDto create(CreateAppointmentRequest request) {
        Patient p = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Пациент не найден: id=" + request.getPatientId()));
        Doctor d = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Врач не найден: id=" + request.getDoctorId()));
        Hospital h = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Больница не найдена: id=" + request.getHospitalId()));

        if (!appointmentRepository.findByDoctorIdAndAppointmentDateTime(d.getId(), request.getAppointmentDateTime()).isEmpty()) {
            throw new BusinessException("Врач уже занят в это время");
        }

        Appointment a = Appointment.builder()
                .patient(p)
                .doctor(d)
                .hospital(h)
                .appointmentDateTime(request.getAppointmentDateTime())
                .reason(request.getReason())
                .status(AppointmentStatus.PENDING)
                .build();

        AppointmentDto dto = toDto(appointmentRepository.save(a));

        // Observer: publish domain event
        eventPublisher.publishEvent(new AppointmentCreatedEvent(this, dto));
        // Singleton: update shared runtime counter
        statsService.incrementAppointments();

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentDto> findAll(Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findAll(pageable);
        List<AppointmentDto> dtos = page.getContent().stream().map(this::toDto).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    public void cancel(Long id) {
        Appointment a = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Запись не найдена: id=" + id));
        AppointmentStatus previous = a.getStatus();
        a.transitionTo(AppointmentStatus.CANCELLED);
        appointmentRepository.save(a);

        // Observer: publish status change event
        String patientEmail = a.getPatient() != null ? a.getPatient().getEmail() : null;
        String patientName  = a.getPatient() != null
                ? a.getPatient().getFirstName() + " " + a.getPatient().getLastName() : "";
        eventPublisher.publishEvent(new AppointmentStatusChangedEvent(
                this, id, patientEmail, patientName, previous, AppointmentStatus.CANCELLED));
    }

    private AppointmentDto toDto(Appointment a) {
        AppointmentDto dto = new AppointmentDto();
        dto.setId(a.getId());
        if (a.getPatient() != null) {
            dto.setPatientId(a.getPatient().getId());
            dto.setPatientName(a.getPatient().getFirstName() + " " + a.getPatient().getLastName());
            dto.setPatientEmail(a.getPatient().getEmail());
        }
        if (a.getDoctor() != null) {
            dto.setDoctorId(a.getDoctor().getId());
            dto.setDoctorName(a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName()
                    + " (" + a.getDoctor().getSpecialty() + ")");
        }
        if (a.getHospital() != null) {
            dto.setHospitalId(a.getHospital().getId());
            dto.setHospitalName(a.getHospital().getName());
        }
        dto.setAppointmentDateTime(a.getAppointmentDateTime());
        dto.setReason(a.getReason());
        dto.setStatus(a.getStatus() != null ? a.getStatus().name() : null);
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }
}
