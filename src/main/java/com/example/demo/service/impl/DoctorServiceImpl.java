package com.example.demo.service.impl;

import com.example.demo.dto.DoctorFormDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Doctor;
import com.example.demo.model.Hospital;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.HospitalRepository;
import com.example.demo.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository,
                              HospitalRepository hospitalRepository) {
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Doctor> findPage(Pageable pageable) {
        return doctorRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Doctor> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) return findPage(pageable);
        return doctorRepository.search(query.trim(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Doctor findById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Врач не найден: id=" + id));
    }

    @Override
    public Doctor create(DoctorFormDto dto) {
        Hospital hospital = resolveHospital(dto.getHospitalId());
        Doctor doctor = Doctor.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .specialty(dto.getSpecialty())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .hospital(hospital)
                .build();
        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor update(Long id, DoctorFormDto dto) {
        Doctor doctor = findById(id);
        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        doctor.setHospital(resolveHospital(dto.getHospitalId()));
        return doctorRepository.save(doctor);
    }

    @Override
    public void delete(Long id) {
        Doctor doctor = findById(id);
        doctorRepository.delete(doctor);
    }

    private Hospital resolveHospital(Long hospitalId) {
        if (hospitalId == null) return null;
        return hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Больница не найдена: id=" + hospitalId));
    }
}
