# Лабораторные работы 8-11 (Spring Boot, Security, JPA, Email, MVP)

---

## Лабораторная работа 8-9 / Зертханалық жұмыс 8-9
### Админ-панель и управление пользователями / Әкімшілік панель, пайдаланушыларды басқару

**Цель:**
- Рус: Реализовать административную панель в Spring Boot, добавить CRUD пользователей, настроить доступ по ролям.
- Каз: Spring Boot веб-қосымшасында админ панель құру, пайдаланушыларды басқару (қосу, өзгерту, жою), рольдер арқылы қолжетімділікті басқару.

**Теория:**
- Роли: ROLE_USER (обычный), ROLE_ADMIN (админ)
- Админ может: просматривать, добавлять, редактировать, удалять пользователей

**User Entity:**
```java
@Entity
public class User {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 private String username;
 private String password;
 private String role;
}
```

**Spring Security config:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
 @Bean
 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  http
   .authorizeHttpRequests(auth -> auth
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/login","/register").permitAll()
    .anyRequest().authenticated()
   )
   .formLogin(form -> form.loginPage("/login").permitAll())
   .logout(logout -> logout.permitAll());
  return http.build();
 }
}
```

**AdminController:**
```java
@Controller
@RequestMapping("/admin")
public class AdminController {
 @Autowired
 private UserRepository userRepository;
 @GetMapping("/users")
 public String getUsers(Model model) {
  model.addAttribute("users", userRepository.findAll());
  return "admin/users";
 }
}
```

**Thymeleaf (users.html):**
```html
<h2>Users List</h2>
<table>
<tr><th>ID</th><th>Username</th><th>Role</th></tr>
<tr th:each="user : ${users}">
 <td th:text="${user.id}"></td>
 <td th:text="${user.username}"></td>
 <td th:text="${user.role}"></td>
</tr>
</table>
```

**CRUD:**
- Удаление: /admin/delete/{id}
- Создание: /admin/create (GET/POST)
- Редактирование: /admin/edit/{id}

**Контрольные вопросы:**
- Для чего роли в Spring Security?
- Для чего @PreAuthorize?
- Функция AdminController?

---

## Лабораторная работа 10 / Зертханалық жұмыс 10
### Email-уведомления / Email арқылы хабарлама жіберу

**Цель:**
- Рус: Реализовать отправку email уведомлений пользователю.
- Каз: Email хабарлама жіберуді жүзеге асыру.

**Теория:**
- Используется Spring Mail (JavaMailSender)

**Зависимость:**
```xml
<dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

**application.properties:**
```
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**EmailService:**
```java
@Service
public class EmailService {
 @Autowired
 private JavaMailSender mailSender;
 public void sendEmail(String to, String subject, String text){
  SimpleMailMessage message = new SimpleMailMessage();
  message.setTo(to);
  message.setSubject(subject);
  message.setText(text);
  mailSender.send(message);
 }
}
```

**Контроллер:**
```java
@RestController
@RequestMapping("/email")
public class EmailController {
 @Autowired
 private EmailService emailService;
 @PostMapping("/send")
 public String send(){
  emailService.sendEmail(
   "user@mail.com",
   "Hello",
   "Test message"
  );
  return "Email sent";
 }
}
```

**Практика:**
- Отправка письма при регистрации
- Уведомление при смене пароля

**Контрольные вопросы:**
- Что такое JavaMailSender?
- Что такое SMTP сервер?
- Где находится email-сервис?

---

## Лабораторная работа 11 / Зертханалық жұмыс 11
### Бизнес-логика и MVP / Бизнес логика және MVP

**Цель:**
- Рус: Реализовать проверку бизнес-логики и MVP.
- Каз: Бизнес-логиканы тексеру және MVP құру.

**Теория:**
- Бизнес-логика — правила работы системы
- MVP — минимально жизнеспособный продукт

**Валидация:**
```java
@NotNull
@Size(min = 3)
private String username;
```

**Service-уровень:**
```java
public void register(User user){
 if(userRepository.existsByUsername(user.getUsername())){
  throw new RuntimeException("User already exists");
 }
 userRepository.save(user);
}
```

**Бизнес-правила:**
- Пользователь уникальный
- Пароль минимум 6 символов
- Email валидный

**MVP:**
- Регистрация
- Авторизация
- Просмотр пользователей
- Роли

**Exception Handling:**
```java
@ExceptionHandler(Exception.class)
public ResponseEntity<String> handle(Exception e){
 return ResponseEntity.badRequest().body(e.getMessage());
}
```

**Контрольные вопросы:**
- Что такое бизнес-логика?
- Для чего нужен MVP?
- Где реализуется validation?

---

> Все примеры и теоретические материалы приведены для ознакомления и самостоятельной реализации в проекте.

