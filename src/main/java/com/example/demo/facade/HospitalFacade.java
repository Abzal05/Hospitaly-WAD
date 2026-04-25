package com.example.demo.facade;

import com.example.demo.dto.AppointmentDto;
import com.example.demo.dto.CreateAppointmentRequest;
import com.example.demo.model.Doctor;
import com.example.demo.model.Patient;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.DoctorService;
import com.example.demo.service.PatientService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facade Pattern — provides a single simplified interface to the
 * complex subsystem of Hospital services (Doctor, Patient, Appointment).
 *
 * The HomeController uses this facade instead of injecting multiple services,
 * keeping the controller thin and delegating aggregation logic here.
 */
@Component
public class HospitalFacade {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public HospitalFacade(DoctorService doctorService,
                           PatientService patientService,
                           AppointmentService appointmentService,
                           DoctorRepository doctorRepository,
                           PatientRepository patientRepository,
                           AppointmentRepository appointmentRepository,
                           UserRepository userRepository) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    /** Returns aggregated dashboard stats in a single call. */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("doctorCount", doctorRepository.count());
        stats.put("patientCount", patientRepository.count());
        stats.put("appointmentCount", appointmentRepository.count());
        stats.put("userCount", userRepository.count());
        stats.put("recentDoctors", doctorService.findPage(PageRequest.of(0, 5)).getContent());
        stats.put("recentPatients", patientService.findPage(PageRequest.of(0, 5)).getContent());
        stats.put("recentAppointments", appointmentService.findAll(PageRequest.of(0, 5)).getContent());
        return stats;
    }

    /** Search across doctors and patients simultaneously. */
    public Map<String, Object> search(String query) {
        Map<String, Object> results = new HashMap<>();
        results.put("doctors", doctorService.search(query, PageRequest.of(0, 10)).getContent());
        results.put("patients", patientService.search(query, PageRequest.of(0, 10)).getContent());
        return results;
    }

    /** Book an appointment through the facade (single entry point). */
    public AppointmentDto bookAppointment(CreateAppointmentRequest request) {
        return appointmentService.create(request);
    }

    /** Cancel an appointment through the facade. */
    public void cancelAppointment(Long id) {
        appointmentService.cancel(id);
    }
}
