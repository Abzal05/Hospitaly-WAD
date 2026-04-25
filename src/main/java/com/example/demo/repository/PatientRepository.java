package com.example.demo.repository;

import com.example.demo.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.lastName)  LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.email)     LIKE LOWER(CONCAT('%',:q,'%'))")
    Page<Patient> search(@Param("q") String query, Pageable pageable);
}
