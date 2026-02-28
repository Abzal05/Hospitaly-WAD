package com.example.demo.service.impl;

import com.example.demo.dto.AppointmentDto;
import com.example.demo.dto.CreateAppointmentRequest;
import com.example.demo.model.Appointment;
import com.example.demo.model.Doctor;
import com.example.demo.model.Hospital;
import com.example.demo.model.Patient;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.HospitalRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.service.AppointmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, HospitalRepository hospitalRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
    }

    @Override
    public AppointmentDto create(CreateAppointmentRequest request) {
        Patient p = patientRepository.findById(request.getPatientId()).orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        Doctor d = doctorRepository.findById(request.getDoctorId()).orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        Hospital h = hospitalRepository.findById(request.getHospitalId()).orElseThrow(() -> new IllegalArgumentException("Hospital not found"));

        // check conflicts
        if (!appointmentRepository.findByDoctorIdAndAppointmentDateTime(d.getId(), request.getAppointmentDateTime()).isEmpty()) {
            throw new IllegalArgumentException("Doctor is already booked at this time");
        }

        Appointment a = new Appointment();
        a.setPatient(p);
        a.setDoctor(d);
        a.setHospital(h);
        a.setAppointmentDateTime(request.getAppointmentDateTime());
        a.setReason(request.getReason());
        a.setStatus("PENDING");

        Appointment saved = appointmentRepository.save(a);
        return toDto(saved);
    }

    @Override
    public Page<AppointmentDto> findAll(Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findAll(pageable);
        List<AppointmentDto> dtos = page.getContent().stream().map(this::toDto).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    public void cancel(Long id) {
        Appointment a = appointmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        a.setStatus("CANCELLED");
        appointmentRepository.save(a);
    }

    private AppointmentDto toDto(Appointment a) {
        AppointmentDto dto = new AppointmentDto();
        dto.setId(a.getId());
        dto.setPatientId(a.getPatient() != null ? a.getPatient().getId() : null);
        dto.setDoctorId(a.getDoctor() != null ? a.getDoctor().getId() : null);
        dto.setHospitalId(a.getHospital() != null ? a.getHospital().getId() : null);
        dto.setAppointmentDateTime(a.getAppointmentDateTime());
        dto.setReason(a.getReason());
        dto.setStatus(a.getStatus());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }
}

