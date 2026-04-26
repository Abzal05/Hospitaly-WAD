# 🏥 Hospital Management System

**Spring Boot · Spring Security · JPA · Thymeleaf · REST API · JWT · 8 Design Patterns**

> Автор: **Арон Абзал** — ВТиПО-33 · [GitHub: Abzal05](https://github.com/Abzal05)

---

## 🚀 Запуск

```bash
git clone https://github.com/Abzal05/Hospitaly-WAD.git
cd Hospitaly-WAD
./gradlew bootRun
```

Открыть: **http://localhost:8081**  
Войти как администратор: `admin` / `admin123`

---

## ✅ Лабораторная №8–9 — Spring Security + Панель администратора

### Role-based access control (ROLE_USER, ROLE_ADMIN)

```java
// SecurityConfig.java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/profile/**").authenticated()
                .requestMatchers("/api/auth/**", "/login", "/css/**", "/js/**").permitAll()
                .anyRequest().permitAll()
            )
            .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/").permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout"))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

### AdminController — полный CRUD пользователей

```java
// AdminController.java
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/users/create")
    public String createSubmit(@Valid @ModelAttribute("userDto") UserEditDto dto,
                               BindingResult result, Model model,
                               RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());
            return "admin/user-create";
        }
        userService.create(dto);
        redirectAttrs.addFlashAttribute("success", "Пользователь создан");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes ra) {
        userService.toggleEnabled(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        userService.delete(id);
        return "redirect:/admin/users";
    }
}
```

### Thymeleaf — таблица пользователей с email и ролями

```html
<!-- admin/users.html -->
<table class="admin-table">
    <thead>
        <tr>
            <th>ID</th><th>Логин</th><th>Email</th>
            <th>Роли</th><th>Статус</th><th>Создан</th><th>Действия</th>
        </tr>
    </thead>
    <tbody>
        <tr th:each="u : ${users}">
            <td th:text="${u.id}"></td>
            <td th:text="${u.username}"></td>
            <td th:text="${u.email != null ? u.email : '—'}"></td>
            <td>
                <span th:each="r : ${u.roles}" th:text="${r.name}"
                      th:classappend="${r.name == 'ROLE_ADMIN'} ? 'badge-admin' : 'badge'">
                </span>
            </td>
            <td>
                <span th:if="${u.enabled}" class="status-active">Активен</span>
                <span th:unless="${u.enabled}" class="status-disabled">Отключён</span>
            </td>
            <td th:text="${#temporals.format(u.createdAt, 'dd.MM.yyyy')}"></td>
            <td>
                <a th:href="@{/admin/users/{id}/edit(id=${u.id})}" class="btn btn-sm">Изменить</a>
                <form th:action="@{/admin/users/{id}/delete(id=${u.id})}" method="post" style="display:inline">
                    <button class="btn btn-sm btn-danger">Удалить</button>
                </form>
            </td>
        </tr>
    </tbody>
</table>
```

### JWT — двойная аутентификация (форма + REST API)

```java
// JwtTokenProvider.java
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    public String createToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

```bash
# Получить JWT токен через REST API:
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Ответ:
{ "token": "eyJhbGciOiJIUzI1NiJ9...", "tokenType": "Bearer" }
```

---

## ✅ Лабораторная №10 — Email уведомления

### EmailService с @Async

```java
// EmailService.java
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Async
    public void sendWelcomeEmail(String to, String username) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Добро пожаловать в Hospital System!");
        msg.setText("Здравствуйте, " + username + "!\n\nВаш аккаунт успешно создан.");
        mailSender.send(msg);
    }
}
```

### Strategy Pattern — консоль или реальный SMTP

```java
// NotificationStrategy.java — интерфейс
public interface NotificationStrategy {
    String getName();
    void send(String recipient, String subject, String body);
}

// ConsoleNotificationStrategy.java — для разработки
@Component
public class ConsoleNotificationStrategy implements NotificationStrategy {
    @Override
    public String getName() { return "console"; }

    @Override
    public void send(String recipient, String subject, String body) {
        System.out.printf("[NOTIFICATION] To: %s | %s%n", recipient, subject);
    }
}

// EmailNotificationStrategy.java — для продакшна
@Component
public class EmailNotificationStrategy implements NotificationStrategy {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Override
    public String getName() { return "email"; }

    @Override
    public void send(String recipient, String subject, String body) {
        if (!mailEnabled || mailSender == null) {
            System.out.printf("[email-disabled] To: %s | %s%n", recipient, subject);
            return;
        }
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(recipient);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }
}
```

### Observer Pattern — события при регистрации

```java
// UserServiceImpl.java — публикация события
eventPublisher.publishEvent(new UserRegisteredEvent(this, savedUser));

// DomainEventListener.java — подписчик
@Component
public class DomainEventListener {

    @EventListener
    @Async
    public void onUserRegistered(UserRegisteredEvent event) {
        notificationService.sendWelcome(
            event.getUser().getEmail(),
            event.getUser().getUsername()
        );
    }

    @EventListener
    @Async
    public void onAppointmentCreated(AppointmentCreatedEvent event) {
        notificationService.sendAppointmentConfirmed(
            event.getAppointment().getPatient().getEmail(),
            event.getAppointment().getDoctor().getFirstName()
        );
    }
}
```

### application.properties — SMTP настройки

```properties
# Включить реальную отправку:
#   1. app.mail.enabled=true
#   2. Заменить username/password на Gmail + App Password
#   3. app.notification.strategy=email

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

app.mail.enabled=false
app.notification.strategy=console
```

---

## ✅ Лабораторная №11 — Bean Validation + GlobalExceptionHandler

### @Valid — валидация на всех уровнях

```java
// UserEditDto.java — аннотации ограничений
@Data
public class UserEditDto {

    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 50, message = "Логин: от 3 до 50 символов")
    private String username;

    @Email(message = "Некорректный адрес электронной почты")
    private String email;

    @Size(min = 4, message = "Пароль: минимум 4 символа")
    private String password;

    private boolean enabled = true;
    private Set<String> roleNames = new HashSet<>();
}

// LoginRequest.java
@Data
public class LoginRequest {

    @NotBlank(message = "Логин обязателен")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    private String password;
}

// CreateAppointmentRequest.java
@Data
public class CreateAppointmentRequest {

    @NotNull(message = "Пациент обязателен")
    private Long patientId;

    @NotNull(message = "Врач обязателен")
    private Long doctorId;

    @NotNull(message = "Дата и время обязательны")
    @Future(message = "Дата приёма должна быть в будущем")
    private LocalDateTime appointmentDateTime;

    @NotBlank(message = "Причина визита обязательна")
    private String reason;
}
```

### Inline-ошибки в Thymeleaf-форме

```html
<!-- admin/user-create.html -->
<div class="form-field">
    <label>Логин *</label>
    <input type="text" th:field="*{username}" class="input" placeholder="минимум 3 символа"/>
    <span th:if="${#fields.hasErrors('username')}"
          th:errors="*{username}"
          class="field-error">
    </span>
</div>

<div class="form-field">
    <label>Email</label>
    <input type="email" th:field="*{email}" class="input" placeholder="user@example.com"/>
    <span th:if="${#fields.hasErrors('email')}"
          th:errors="*{email}"
          class="field-error">
    </span>
</div>
```

### GlobalExceptionHandler — @ControllerAdvice для MVC

```java
// GlobalExceptionHandler.java
@ControllerAdvice(basePackages = "com.example.demo.controller.mvc")
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        return errorView(404, ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusiness(BusinessException ex) {
        return errorView(400, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDenied(AccessDeniedException ex) {
        return errorView(403, "Доступ запрещён. Недостаточно прав.");
    }

    private ModelAndView errorView(int status, String message) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", status);
        mav.addObject("message", message);
        return mav;
    }
}
```

### RestExceptionHandler — JSON-ошибки для REST API

```java
// RestExceptionHandler.java
@RestControllerAdvice(basePackages = "com.example.demo.controller.api")
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        return Map.of("errors", fieldErrors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(ResourceNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }
}

// Пример — POST /api/auth/login с пустыми полями:
// HTTP 400 Bad Request
// {
//   "errors": {
//     "username": "Логин обязателен",
//     "password": "Пароль обязателен"
//   }
// }
```

### Service layer — вся бизнес-логика в сервисах

```java
// UserServiceImpl.java
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Override
    public User create(UserEditDto dto) {
        // Проверка существования логина — в сервисе, не в контроллере
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("Логин уже занят: " + dto.getUsername());
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new BusinessException("Пароль обязателен при создании");
        }

        User saved = userRepository.save(User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .enabled(dto.isEnabled())
                .roles(resolveRoles(dto.getRoleNames()))
                .build());

        eventPublisher.publishEvent(new UserRegisteredEvent(this, saved));
        statsService.incrementUsers();
        return saved;
    }
}
```

---

## ✅ Паттерны проектирования

### Builder — Lombok @Builder на сущностях

```java
// User.java
@Entity
@Table(name = "users")
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private Instant createdAt = Instant.now();
}

// Использование Builder:
User user = User.builder()
        .username("ivan")
        .email("ivan@mail.kz")
        .password(passwordEncoder.encode("secret"))
        .enabled(true)
        .build();
```

### Lombok @Data на DTO — рефакторинг −360 строк

```java
// ДО: 94 строки с ручными getter/setter
public class PatientDto {
    private Long id;
    private String firstName;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { firstName = v; }
    // ...ещё 80 строк...
}

// ПОСЛЕ: @Data — 16 строк
@Data
public class PatientDto {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private Instant createdAt;
    // getter/setter/equals/hashCode/toString — генерирует Lombok
}

// Рефакторинг применён к 10 классам:
// LoginRequest, RegisterRequest, UserEditDto, DoctorFormDto,
// DoctorDto, PatientFormDto, PatientDto, AppointmentDto,
// CreateAppointmentRequest, LoginResponse
```

### Factory — создание счетов по типу

```java
// AccountFactory.java
@Component
public class AccountFactory {

    public Account create(AccountType type, User user) {
        return Account.builder()
                .accountType(type)
                .user(user)
                .accountNumber(generateNumber(type))
                .balance(defaultBalance(type))
                .build();
    }

    private String generateNumber(AccountType type) {
        String prefix = switch (type) {
            case SAVINGS -> "SAV";
            case CREDIT  -> "CRD";
            case DEBIT   -> "DBT";
        };
        return prefix + "-" + System.currentTimeMillis();
    }

    private BigDecimal defaultBalance(AccountType type) {
        return switch (type) {
            case CREDIT -> new BigDecimal("5000.00");
            default     -> BigDecimal.ZERO;
        };
    }
}
```

### State Machine — переходы статусов приёма

```java
// AppointmentStatus.java
public enum AppointmentStatus {
    PENDING, SCHEDULED, COMPLETED, CANCELLED, FAILED;

    public boolean canTransitionTo(AppointmentStatus next) {
        return switch (this) {
            case PENDING    -> next == SCHEDULED || next == CANCELLED;
            case SCHEDULED  -> next == COMPLETED || next == CANCELLED;
            case COMPLETED,
                 CANCELLED,
                 FAILED     -> false; // финальные состояния
        };
    }
}

// Appointment.java
public void transitionTo(AppointmentStatus next) {
    if (!this.status.canTransitionTo(next)) {
        throw new IllegalStateException(
            "Нельзя перейти из " + this.status + " в " + next
        );
    }
    this.status = next;
}
```

### Facade — агрегирует несколько сервисов

```java
// HospitalFacade.java
@Component
public class HospitalFacade {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public Map<String, Object> getDashboardStats() {
        return Map.of(
            "doctors",      doctorService.findAll().size(),
            "patients",     patientService.findAll().size(),
            "appointments", appointmentService.findAll(Pageable.unpaged()).getTotalElements()
        );
    }
}
```

### Singleton — глобальный счётчик статистики

```java
// SystemStatsService.java
@Service
@Scope("singleton")
public class SystemStatsService {

    private final AtomicLong appointmentsCreated = new AtomicLong(0);
    private final AtomicLong usersRegistered    = new AtomicLong(0);
    private final Instant startTime             = Instant.now();

    public void incrementAppointments() { appointmentsCreated.incrementAndGet(); }
    public void incrementUsers()        { usersRegistered.incrementAndGet(); }

    public long getUptimeSeconds() {
        return Duration.between(startTime, Instant.now()).getSeconds();
    }
}
```

---

## ✅ Профиль через SecurityContextHolder

```java
// ProfileController.java
@Controller
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping
    public String profile(Model model) {
        // Данные берутся из контекста залогиненного пользователя
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login";
        }

        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        model.addAttribute("user", user);
        model.addAttribute("accounts", accountRepository.findByUserId(user.getId()));
        model.addAttribute("isAdmin", auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        return "profile";
    }
}
```

```html
<!-- profile.html — отображение данных текущего пользователя -->
<div style="display:grid;grid-template-columns:1fr 1fr;gap:1rem">
    <div>
        <div style="color:var(--muted)">ID пользователя</div>
        <div th:text="${user.id}"></div>
    </div>
    <div>
        <div style="color:var(--muted)">Email</div>
        <div th:text="${user.email != null ? user.email : '—'}"></div>
    </div>
    <div>
        <div style="color:var(--muted)">Дата регистрации</div>
        <div th:text="${#temporals.format(user.createdAt, 'dd.MM.yyyy HH:mm')}"></div>
    </div>
    <div>
        <div style="color:var(--muted)">Права доступа</div>
        <span th:if="${isAdmin}" style="color:var(--primary);font-weight:700">Администратор</span>
        <span th:unless="${isAdmin}">Пользователь</span>
    </div>
</div>
```

---

## ✅ Thymeleaf Fragments — переиспользуемые компоненты

```html
<!-- fragments/header.html -->
<header th:fragment="navbar" class="header">
    <div class="brand">
        <div class="logo">🏥</div>
        <div class="site-title">Управление больницей</div>
    </div>
    <nav class="nav">
        <a th:href="@{/}">Главная</a>
        <a th:href="@{/doctors}">Врачи</a>
        <a th:href="@{/patients}">Пациенты</a>
        <a th:href="@{/profile}" sec:authorize="isAuthenticated()">Профиль</a>
        <a th:href="@{/admin/users}" sec:authorize="hasRole('ADMIN')"
           style="background:var(--primary);color:white;padding:0.3rem 0.7rem;border-radius:8px">
            Админ
        </a>
        <a th:href="@{/login}" sec:authorize="!isAuthenticated()">Вход</a>
        <form th:action="@{/logout}" method="post" sec:authorize="isAuthenticated()">
            <button type="submit">Выйти</button>
        </form>
    </nav>
</header>

<!-- Подключение фрагмента в любой странице: -->
<header th:replace="~{fragments/header :: navbar}"></header>
<footer th:replace="~{fragments/footer :: footer}"></footer>
<nav    th:replace="~{fragments/sidebar :: adminSidebar}"></nav>
```

---

## 📁 Структура проекта

```
src/main/java/com/example/demo/
├── config/
│   ├── AsyncConfig.java              — @EnableAsync для @Async методов
│   ├── DataInitializer.java          — тестовые данные при запуске
│   ├── SecurityBeansConfig.java      — PasswordEncoder, AuthManager
│   └── SecurityConfig.java           — JWT фильтр, роли, маршруты
├── controller/
│   ├── api/                          — REST контроллеры
│   │   ├── AuthRestController.java   — POST /api/auth/login|register
│   │   ├── DoctorRestController.java — GET /api/doctors
│   │   ├── PatientRestController.java
│   │   ├── AppointmentRestController.java
│   │   └── EmailController.java
│   └── mvc/                          — MVC контроллеры
│       ├── AdminController.java      — CRUD пользователей
│       ├── AdminDoctorController.java
│       ├── AdminPatientController.java
│       ├── AdminAccountController.java
│       ├── HomeController.java
│       ├── ProfileController.java    — SecurityContextHolder
│       └── UiController.java
├── dto/                              — @Data Lombok (10 классов)
├── event/                            — Observer: UserRegistered, AppointmentCreated
├── exception/
│   ├── BusinessException.java
│   ├── ResourceNotFoundException.java
│   ├── GlobalExceptionHandler.java   — @ControllerAdvice (MVC)
│   └── RestExceptionHandler.java     — @RestControllerAdvice (API)
├── facade/
│   └── HospitalFacade.java           — Facade Pattern
├── factory/
│   └── AccountFactory.java           — Factory Pattern
├── model/
│   ├── User.java                     — @Builder
│   ├── Account.java                  — @Builder
│   ├── Transaction.java              — @Builder + State transitions
│   ├── Appointment.java              — State Machine
│   ├── AppointmentStatus.java        — Enum + canTransitionTo()
│   ├── AccountType.java              — Enum (SAVINGS, CREDIT, DEBIT)
│   └── TransactionStatus.java        — Enum (PENDING, COMPLETED, FAILED)
├── repository/                       — Repository Pattern (Spring Data JPA)
├── security/
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
├── service/                          — вся бизнес-логика
│   └── impl/
│       ├── UserServiceImpl.java
│       ├── AppointmentServiceImpl.java
│       ├── DoctorServiceImpl.java
│       └── PatientServiceImpl.java
└── strategy/                         — Strategy Pattern
    ├── NotificationStrategy.java     — интерфейс
    ├── ConsoleNotificationStrategy.java
    └── EmailNotificationStrategy.java

src/main/resources/
├── application.properties
├── static/css/style.css
├── static/js/app.js
└── templates/
    ├── index.html
    ├── login.html
    ├── doctors.html
    ├── patients.html
    ├── profile.html
    ├── fragments/
    │   ├── header.html
    │   ├── footer.html
    │   └── sidebar.html
    └── admin/
        ├── users.html
        ├── user-create.html
        ├── user-edit.html
        ├── doctors.html
        ├── doctor-create.html
        ├── doctor-edit.html
        ├── patients.html
        ├── patient-create.html
        ├── patient-edit.html
        └── accounts.html
```

---

## 🔌 REST API

| Метод | URL | Описание | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Регистрация | ❌ |
| POST | `/api/auth/login` | JWT токен | ❌ |
| GET | `/api/doctors` | Список врачей | ✅ |
| GET | `/api/doctors/{id}` | Врач по ID | ✅ |
| GET | `/api/patients` | Список пациентов | ✅ |
| GET | `/api/patients/{id}` | Пациент по ID | ✅ |
| GET | `/api/appointments` | Список приёмов | ✅ |
| POST | `/api/appointments` | Создать приём | ✅ |
| PUT | `/api/appointments/{id}/cancel` | Отменить | ✅ |
| POST | `/email/send` | Отправить email | ADMIN |

H2 консоль: **http://localhost:8081/h2-console**

---

## 👤 Автор

**Арон Абзал** — ВТиПО-33  
GitHub: [Abzal05](https://github.com/Abzal05)
