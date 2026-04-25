package com.example.demo.service;

import com.example.demo.dto.PatientFormDto;
import com.example.demo.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {
    List<Patient> findAll();
    Page<Patient> findPage(Pageable pageable);
    Page<Patient> search(String query, Pageable pageable);
    Patient findById(Long id);
    Patient create(PatientFormDto dto);
    Patient update(Long id, PatientFormDto dto);
    void delete(Long id);
}
