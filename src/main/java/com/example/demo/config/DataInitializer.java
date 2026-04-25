package com.example.demo.config;

import com.example.demo.factory.AccountFactory;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountFactory accountFactory;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           HospitalRepository hospitalRepository,
                           DoctorRepository doctorRepository,
                           PatientRepository patientRepository,
                           AppointmentRepository appointmentRepository,
                           AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           AccountFactory accountFactory,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hospitalRepository = hospitalRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.accountFactory = accountFactory;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Роли
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        // Пользователи
        User admin = null;
        if (userRepository.findByUsername("admin").isEmpty()) {
            admin = userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .enabled(true)
                    .roles(Set.of(adminRole, userRole))
                    .build());
            System.out.println("✓ Создан admin (login: admin, password: admin)");
        } else {
            admin = userRepository.findByUsername("admin").get();
        }

        User testUser = null;
        if (userRepository.findByUsername("user1").isEmpty()) {
            testUser = userRepository.save(User.builder()
                    .username("user1")
                    .password(passwordEncoder.encode("user1"))
                    .enabled(true)
                    .roles(Set.of(userRole))
                    .build());
            System.out.println("✓ Создан user1 (login: user1, password: user1)");
        } else {
            testUser = userRepository.findByUsername("user1").get();
        }

        // Больницы
        if (hospitalRepository.count() == 0) {
            Hospital h1 = new Hospital();
            h1.setName("Городская клиническая больница №1");
            h1.setAddress("г. Алматы, ул. Абая 123");
            h1.setPhone("+7 (727) 123-45-67");
            hospitalRepository.save(h1);

            Hospital h2 = new Hospital();
            h2.setName("Медицинский центр Здоровье");
            h2.setAddress("г. Алматы, пр. Назарбаева 45");
            h2.setPhone("+7 (727) 987-65-43");
            hospitalRepository.save(h2);
            System.out.println("✓ Созданы больницы");
        }

        // Врачи
        if (doctorRepository.count() == 0) {
            Hospital h1 = hospitalRepository.findAll().get(0);
            Hospital h2 = hospitalRepository.findAll().size() > 1 ? hospitalRepository.findAll().get(1) : h1;

            doctorRepository.save(Doctor.builder().firstName("Айгуль").lastName("Смагулова")
                    .specialty("Кардиолог").email("a.smagulova@hospital.kz").phone("+7 701 234 56 78").hospital(h1).build());
            doctorRepository.save(Doctor.builder().firstName("Ержан").lastName("Касымов")
                    .specialty("Терапевт").email("e.kasymov@hospital.kz").phone("+7 702 345 67 89").hospital(h1).build());
            doctorRepository.save(Doctor.builder().firstName("Марина").lastName("Петрова")
                    .specialty("Невролог").email("m.petrova@hospital.kz").phone("+7 705 456 78 90").hospital(h2).build());
            doctorRepository.save(Doctor.builder().firstName("Асхат").lastName("Нурланов")
                    .specialty("Хирург").email("a.nurlanov@hospital.kz").phone("+7 707 567 89 01").hospital(h2).build());
            doctorRepository.save(Doctor.builder().firstName("Динара").lastName("Абдуллаева")
                    .specialty("Педиатр").email("d.abdullaeva@hospital.kz").phone("+7 708 678 90 12").hospital(h1).build());
            System.out.println("✓ Созданы врачи");
        }

        // Пациенты
        if (patientRepository.count() == 0) {
            patientRepository.save(Patient.builder().firstName("Алма").lastName("Жумабаева")
                    .email("alma.j@mail.kz").phone("+7 701 111 22 33")
                    .dob(LocalDate.of(1985, 5, 15)).gender("Женский").address("г. Алматы, мкр. Самал-2, д. 45").build());
            patientRepository.save(Patient.builder().firstName("Ермек").lastName("Сулейменов")
                    .email("yermek.s@mail.kz").phone("+7 702 222 33 44")
                    .dob(LocalDate.of(1992, 8, 20)).gender("Мужской").address("г. Алматы, ул. Розыбакиева, д. 78").build());
            patientRepository.save(Patient.builder().firstName("Саулет").lastName("Бекбосынов")
                    .email("saulet.b@mail.kz").phone("+7 705 333 44 55")
                    .dob(LocalDate.of(1978, 12, 10)).gender("Мужской").address("г. Алматы, пр. Достык, д. 123").build());
            patientRepository.save(Patient.builder().firstName("Гульнара").lastName("Омарова")
                    .email("gulnara.o@mail.kz").phone("+7 707 444 55 66")
                    .dob(LocalDate.of(1990, 3, 25)).gender("Женский").address("г. Алматы, мкр. Аксай-3, д. 89").build());
            System.out.println("✓ Созданы пациенты");
        }

        // Записи на приём
        if (appointmentRepository.count() == 0) {
            List<Doctor> doctors = doctorRepository.findAll();
            List<Patient> patients = patientRepository.findAll();
            List<Hospital> hospitals = hospitalRepository.findAll();

            if (!doctors.isEmpty() && !patients.isEmpty()) {
                appointmentRepository.save(Appointment.builder()
                        .patient(patients.get(0)).doctor(doctors.get(0)).hospital(hospitals.get(0))
                        .appointmentDateTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0))
                        .reason("Консультация кардиолога").status(AppointmentStatus.SCHEDULED).build());

                appointmentRepository.save(Appointment.builder()
                        .patient(patients.get(1)).doctor(doctors.get(1)).hospital(hospitals.get(0))
                        .appointmentDateTime(LocalDateTime.now().plusDays(1).withHour(14).withMinute(30))
                        .reason("Общий осмотр").status(AppointmentStatus.SCHEDULED).build());

                if (patients.size() > 2 && doctors.size() > 2) {
                    appointmentRepository.save(Appointment.builder()
                            .patient(patients.get(2)).doctor(doctors.get(2))
                            .hospital(hospitals.size() > 1 ? hospitals.get(1) : hospitals.get(0))
                            .appointmentDateTime(LocalDateTime.now().plusDays(3).withHour(11).withMinute(0))
                            .reason("Головные боли").status(AppointmentStatus.SCHEDULED).build());
                }
                System.out.println("✓ Созданы записи на приём");
            }
        }

        // Счета (Factory Pattern)
        if (accountRepository.count() == 0) {
            Account savings = accountFactory.create(AccountType.SAVINGS, admin);
            savings.setBalance(new BigDecimal("150000.00"));
            accountRepository.save(savings);

            Account debit = accountFactory.create(AccountType.DEBIT, admin);
            debit.setBalance(new BigDecimal("25000.00"));
            accountRepository.save(debit);

            Account credit = accountFactory.create(AccountType.CREDIT, testUser);
            accountRepository.save(credit);

            // Транзакции с переходами состояний
            Transaction t1 = Transaction.builder()
                    .account(savings).amount(new BigDecimal("50000.00"))
                    .description("Пополнение счёта").build();
            t1.complete();
            transactionRepository.save(t1);

            Transaction t2 = Transaction.builder()
                    .account(debit).amount(new BigDecimal("5000.00"))
                    .description("Оплата услуг").build();
            t2.complete();
            transactionRepository.save(t2);

            Transaction t3 = Transaction.builder()
                    .account(savings).amount(new BigDecimal("1000.00"))
                    .description("Попытка перевода").build();
            t3.fail();
            transactionRepository.save(t3);

            System.out.println("✓ Созданы счета и транзакции");
        }

        System.out.println("=================================");
        System.out.println("✓ База данных инициализирована!");
        System.out.println("  admin / admin  →  полный доступ");
        System.out.println("  user1 / user1  →  обычный пользователь");
        System.out.println("=================================");
    }
}
