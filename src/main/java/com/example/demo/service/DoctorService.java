package com.example.demo.service;

import com.example.demo.dto.DoctorFormDto;
import com.example.demo.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorService {
    List<Doctor> findAll();
    Page<Doctor> findPage(Pageable pageable);
    Page<Doctor> search(String query, Pageable pageable);
    Doctor findById(Long id);
    Doctor create(DoctorFormDto dto);
    Doctor update(Long id, DoctorFormDto dto);
    void delete(Long id);
}
