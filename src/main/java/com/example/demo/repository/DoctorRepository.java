package com.example.demo.repository;

import com.example.demo.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByHospitalId(Long hospitalId);

    @Query("SELECT d FROM Doctor d WHERE " +
           "LOWER(d.firstName) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(d.lastName)  LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(d.specialty) LIKE LOWER(CONCAT('%',:q,'%'))")
    Page<Doctor> search(@Param("q") String query, Pageable pageable);
}
