package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                          RoleRepository roleRepository,
                          HospitalRepository hospitalRepository,
                          DoctorRepository doctorRepository,
                          PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hospitalRepository = hospitalRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Создаём роли
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_ADMIN");
                    return roleRepository.save(r);
                });

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_USER");
                    return roleRepository.save(r);
                });

        // Создаём админа
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setEnabled(true);
            admin.setRoles(Set.of(adminRole, userRole));
            userRepository.save(admin);
            System.out.println("✓ Created admin user (login: admin, password: admin)");
        }

        // Создаём больницу
        if (hospitalRepository.count() == 0) {
            Hospital hospital = new Hospital();
            hospital.setName("Городская клиническая больница №1");
            hospital.setAddress("г. Алматы, ул. Абая 123");
            hospital.setPhone("+7 (727) 123-45-67");
            hospitalRepository.save(hospital);

            Hospital hospital2 = new Hospital();
            hospital2.setName("Медицинский центр Здоровье");
            hospital2.setAddress("г. Алматы, пр. Назарбаева 45");
            hospital2.setPhone("+7 (727) 987-65-43");
            hospitalRepository.save(hospital2);

            System.out.println("✓ Created hospitals");
        }

        // Создаём врачей
        if (doctorRepository.count() == 0) {
            Hospital h1 = hospitalRepository.findAll().get(0);
            Hospital h2 = hospitalRepository.findAll().size() > 1 ? hospitalRepository.findAll().get(1) : h1;

            Doctor d1 = new Doctor();
            d1.setFirstName("Айгуль");
            d1.setLastName("Смагулова");
            d1.setSpecialty("Кардиолог");
            d1.setEmail("a.smagulova@hospital.kz");
            d1.setPhone("+7 701 234 56 78");
            d1.setHospital(h1);
            doctorRepository.save(d1);

            Doctor d2 = new Doctor();
            d2.setFirstName("Ержан");
            d2.setLastName("Касымов");
            d2.setSpecialty("Терапевт");
            d2.setEmail("e.kasymov@hospital.kz");
            d2.setPhone("+7 702 345 67 89");
            d2.setHospital(h1);
            doctorRepository.save(d2);

            Doctor d3 = new Doctor();
            d3.setFirstName("Марина");
            d3.setLastName("Петрова");
            d3.setSpecialty("Невролог");
            d3.setEmail("m.petrova@hospital.kz");
            d3.setPhone("+7 705 456 78 90");
            d3.setHospital(h2);
            doctorRepository.save(d3);

            Doctor d4 = new Doctor();
            d4.setFirstName("Асхат");
            d4.setLastName("Нурланов");
            d4.setSpecialty("Хирург");
            d4.setEmail("a.nurlanov@hospital.kz");
            d4.setPhone("+7 707 567 89 01");
            d4.setHospital(h2);
            doctorRepository.save(d4);

            Doctor d5 = new Doctor();
            d5.setFirstName("Динара");
            d5.setLastName("Абдуллаева");
            d5.setSpecialty("Педиатр");
            d5.setEmail("d.abdullaeva@hospital.kz");
            d5.setPhone("+7 708 678 90 12");
            d5.setHospital(h1);
            doctorRepository.save(d5);

            System.out.println("✓ Created 5 doctors");
        }

        // Создаём пациентов
        if (patientRepository.count() == 0) {
            Patient p1 = new Patient();
            p1.setFirstName("Алма");
            p1.setLastName("Жумабаева");
            p1.setEmail("alma.j@mail.kz");
            p1.setPhone("+7 701 111 22 33");
            p1.setDob(LocalDate.of(1985, 5, 15));
            p1.setGender("Женский");
            p1.setAddress("г. Алматы, мкр. Самал-2, д. 45");
            patientRepository.save(p1);

            Patient p2 = new Patient();
            p2.setFirstName("Ермек");
            p2.setLastName("Сулейменов");
            p2.setEmail("yermek.s@mail.kz");
            p2.setPhone("+7 702 222 33 44");
            p2.setDob(LocalDate.of(1992, 8, 20));
            p2.setGender("Мужской");
            p2.setAddress("г. Алматы, ул. Розыбакиева, д. 78");
            patientRepository.save(p2);

            Patient p3 = new Patient();
            p3.setFirstName("Саулет");
            p3.setLastName("Бекбосынов");
            p3.setEmail("saulet.b@mail.kz");
            p3.setPhone("+7 705 333 44 55");
            p3.setDob(LocalDate.of(1978, 12, 10));
            p3.setGender("Мужской");
            p3.setAddress("г. Алматы, пр. Достык, д. 123");
            patientRepository.save(p3);

            Patient p4 = new Patient();
            p4.setFirstName("Гульнара");
            p4.setLastName("Омарова");
            p4.setEmail("gulnara.o@mail.kz");
            p4.setPhone("+7 707 444 55 66");
            p4.setDob(LocalDate.of(1990, 3, 25));
            p4.setGender("Женский");
            p4.setAddress("г. Алматы, мкр. Аксай-3, д. 89");
            patientRepository.save(p4);

            System.out.println("✓ Created 4 patients");
        }

        // Создаём записи на приём
        if (appointmentRepository.count() == 0) {
            List<Doctor> doctors = doctorRepository.findAll();
            List<Patient> patients = patientRepository.findAll();
            List<Hospital> hospitals = hospitalRepository.findAll();

            if (!doctors.isEmpty() && !patients.isEmpty() && !hospitals.isEmpty()) {
                Appointment a1 = new Appointment();
                a1.setPatient(patients.get(0));
                a1.setDoctor(doctors.get(0));
                a1.setHospital(hospitals.get(0));
                a1.setAppointmentDateTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));
                a1.setReason("Консультация кардиолога");
                a1.setStatus("SCHEDULED");
                appointmentRepository.save(a1);

                Appointment a2 = new Appointment();
                a2.setPatient(patients.get(1));
                a2.setDoctor(doctors.get(1));
                a2.setHospital(hospitals.get(0));
                a2.setAppointmentDateTime(LocalDateTime.now().plusDays(1).withHour(14).withMinute(30));
                a2.setReason("Общий осмотр");
                a2.setStatus("SCHEDULED");
                appointmentRepository.save(a2);

                if (patients.size() > 2 && doctors.size() > 2) {
                    Appointment a3 = new Appointment();
                    a3.setPatient(patients.get(2));
                    a3.setDoctor(doctors.get(2));
                    a3.setHospital(hospitals.size() > 1 ? hospitals.get(1) : hospitals.get(0));
                    a3.setAppointmentDateTime(LocalDateTime.now().plusDays(3).withHour(11).withMinute(0));
                    a3.setReason("Головные боли");
                    a3.setStatus("SCHEDULED");
                    appointmentRepository.save(a3);
                }

                System.out.println("✓ Created appointments");
            }
        }

        System.out.println("=================================");
        System.out.println("✓ Database initialized successfully!");
        System.out.println("=================================");
    }
}

