package com.example.demo.service;

import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Singleton Pattern — explicitly scoped as singleton (Spring default).
 * A single shared instance tracks live counters and system uptime.
 *
 * All callers share the same AtomicLong counters — no static state needed.
 */
@Service
@Scope("singleton")
public class SystemStatsService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    private final AtomicLong appointmentsCreatedSinceStartup = new AtomicLong(0);
    private final AtomicLong usersRegisteredSinceStartup = new AtomicLong(0);
    private long startupTime;

    public SystemStatsService(DoctorRepository doctorRepository,
                               PatientRepository patientRepository,
                               AppointmentRepository appointmentRepository,
                               UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        this.startupTime = System.currentTimeMillis();
        System.out.println("[SystemStatsService] Singleton instance created — id: "
                + System.identityHashCode(this));
    }

    public void incrementAppointments() {
        appointmentsCreatedSinceStartup.incrementAndGet();
    }

    public void incrementUsers() {
        usersRegisteredSinceStartup.incrementAndGet();
    }

    public long getUptimeSeconds() {
        return (System.currentTimeMillis() - startupTime) / 1000;
    }

    public long getDoctorCount()      { return doctorRepository.count(); }
    public long getPatientCount()     { return patientRepository.count(); }
    public long getAppointmentCount() { return appointmentRepository.count(); }
    public long getUserCount()        { return userRepository.count(); }

    public long getAppointmentsCreatedSinceStartup() {
        return appointmentsCreatedSinceStartup.get();
    }

    public long getUsersRegisteredSinceStartup() {
        return usersRegisteredSinceStartup.get();
    }
}
