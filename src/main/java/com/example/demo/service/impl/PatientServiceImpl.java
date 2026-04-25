package com.example.demo.service.impl;

import com.example.demo.dto.PatientFormDto;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import com.example.demo.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Patient> findPage(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Patient> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) return findPage(pageable);
        return patientRepository.search(query.trim(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пациент не найден: id=" + id));
    }

    @Override
    public Patient create(PatientFormDto dto) {
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && patientRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Пациент с таким email уже существует: " + dto.getEmail());
        }
        Patient patient = Patient.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .dob(dto.getDob())
                .gender(dto.getGender())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .build();
        return patientRepository.save(patient);
    }

    @Override
    public Patient update(Long id, PatientFormDto dto) {
        Patient patient = findById(id);

        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && !dto.getEmail().equals(patient.getEmail())
                && patientRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email уже занят: " + dto.getEmail());
        }

        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setDob(dto.getDob());
        patient.setGender(dto.getGender());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setAddress(dto.getAddress());
        return patientRepository.save(patient);
    }

    @Override
    public void delete(Long id) {
        Patient patient = findById(id);
        patientRepository.delete(patient);
    }
}
