# 🏥 Hospital Management System — Система Управления Больницей

Полнофункциональная система управления больницей с **REST API** и веб-интерфейсом, разработанная на **Spring Boot 4 + H2 Database**.

---

## ✨ Возможности

| Модуль | Функциональность |
|---|---|
| 👤 Авторизация | Регистрация, вход, JWT-авторизация, роли (USER, ADMIN) |
| 🏥 Больницы | Управление больницами, информация об учреждении |
| 👨‍⚕️ Врачи | Список врачей, специализации, привязка к больнице |
| 🧑‍🤝‍🧑 Пациенты | Регистрация пациентов, история обращений |
| 📋 Записи (Appointments) | Запись к врачу, управление статусом, отмена |

---

## 🛠 Технологии

| Компонент | Технология |
|---|---|
| Backend | Spring Boot 4.0.3, Spring Data JPA, Hibernate 7 |
| База данных | H2 (in-memory, встроенная) |
| Безопасность | Spring Security, JWT (JJWT 0.11.5), BCrypt |
| Frontend | HTML5, CSS3, JavaScript (Vanilla), Thymeleaf |
| Сборка | Gradle 8+ |
| Java | 17+ |

---

## 🚀 Быстрый старт

### Минимальные требования

- Java JDK 17+
- Gradle (wrapper включён в проект)

### Запуск за 3 шага

```bash
# 1. Клонировать репозиторий
git clone https://github.com/Abzal05/Hospital-WAD.git
cd Hospital-WAD

# 2. Освободить порт (Windows, если занят)
.\clear-port.bat 8081

# 3. Запустить
.\gradlew.bat bootRun
```

Откройте браузер: **http://localhost:8081**

> **Примечание:** Приложение использует встроенную H2 базу данных — данные сбрасываются при перезапуске. При старте автоматически создаются тестовые данные (врачи, пациенты, больницы).

---

## 🔌 REST API

**Base URL:** `http://localhost:8081`

Все защищённые эндпоинты требуют заголовок:
```
Authorization: Bearer <JWT_TOKEN>
```

---

### 🔐 Аутентификация — `/api/auth`

| Метод | URL | Описание | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Регистрация нового пользователя | ❌ |
| POST | `/api/auth/login` | Вход, получение JWT токена | ❌ |

#### POST `/api/auth/register`
```json
// Request
{
  "username": "ivan123",
  "password": "secret123"
}

// Response 200
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### POST `/api/auth/login`
```json
// Request
{
  "username": "ivan123",
  "password": "secret123"
}

// Response 200
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### 👨‍⚕️ Врачи — `/api/doctors`

| Метод | URL | Описание | Auth |
|---|---|---|---|
| GET | `/api/doctors` | Список всех врачей (с пагинацией) | ✅ |
| GET | `/api/doctors/{id}` | Получить врача по ID | ✅ |

#### GET `/api/doctors?page=0&size=20`
```json
// Response 200
{
  "content": [
    {
      "id": 1,
      "firstName": "Иван",
      "lastName": "Петров",
      "specialty": "Кардиология",
      "email": "petrov@hospital.kz",
      "phone": "+7-777-123-4567"
    }
  ],
  "totalElements": 10,
  "totalPages": 1
}
```

---

### 🧑‍🤝‍🧑 Пациенты — `/api/patients`

| Метод | URL | Описание | Auth |
|---|---|---|---|
| GET | `/api/patients` | Список всех пациентов (с пагинацией) | ✅ |
| GET | `/api/patients/{id}` | Получить пациента по ID | ✅ |

#### GET `/api/patients?page=0&size=20`
```json
// Response 200
{
  "content": [
    {
      "id": 1,
      "firstName": "Алия",
      "lastName": "Сейткали",
      "email": "aliya@mail.kz",
      "phone": "+7-700-987-6543",
      "gender": "Женский"
    }
  ],
  "totalElements": 25,
  "totalPages": 2
}
```

---

### 📋 Записи на приём — `/api/appointments`

| Метод | URL | Описание | Auth |
|---|---|---|---|
| GET | `/api/appointments` | Список всех записей (с пагинацией) | ✅ |
| POST | `/api/appointments` | Создать новую запись к врачу | ✅ |
| PUT | `/api/appointments/{id}/cancel` | Отменить запись | ✅ |

#### POST `/api/appointments`
```json
// Request
{
  "patientId": 1,
  "doctorId": 2,
  "hospitalId": 1,
  "appointmentDateTime": "2026-03-15T10:00:00",
  "reason": "Плановый осмотр"
}

// Response 201
{
  "id": 5,
  "patientName": "Алия Сейткали",
  "doctorName": "Иван Петров",
  "status": "SCHEDULED",
  "appointmentDateTime": "2026-03-15T10:00:00"
}
```

#### PUT `/api/appointments/{id}/cancel`
```
// Response 204 No Content
```

---

## 📝 Типичный сценарий работы с API

```
1. POST /api/auth/register  → зарегистрироваться, получить токен
2. POST /api/auth/login     → войти, получить JWT токен
3. GET  /api/doctors        → посмотреть список врачей (токен в заголовке)
4. GET  /api/patients       → посмотреть список пациентов
5. POST /api/appointments   → записаться к врачу
6. PUT  /api/appointments/{id}/cancel → отменить запись
```

---

## 📁 Структура проекта

```
Hospital-WAD/
├── src/main/java/com/example/demo/
│   ├── Demo2Application.java              — точка входа
│   ├── config/
│   │   ├── DataInitializer.java           — начальные тестовые данные
│   │   ├── SecurityConfig.java            — настройка Spring Security + JWT
│   │   └── SecurityBeansConfig.java       — бины безопасности
│   ├── controller/
│   │   ├── api/                           — REST API контроллеры
│   │   │   ├── AuthRestController.java    — /api/auth (регистрация, вход)
│   │   │   ├── DoctorRestController.java  — /api/doctors
│   │   │   ├── PatientRestController.java — /api/patients
│   │   │   └── AppointmentRestController.java — /api/appointments
│   │   └── mvc/                           — MVC контроллеры (веб-интерфейс)
│   │       ├── HomeController.java        — главная страница
│   │       └── UiController.java          — страницы врачей, пациентов
│   ├── dto/                               — DTO объекты
│   ├── model/                             — JPA сущности
│   │   ├── Hospital.java                  — Больница
│   │   ├── Doctor.java                    — Врач
│   │   ├── Patient.java                   — Пациент
│   │   ├── Appointment.java               — Запись на приём
│   │   ├── User.java                      — Пользователь системы
│   │   └── Role.java                      — Роль (USER, ADMIN)
│   ├── repository/                        — Spring Data репозитории
│   ├── security/                          — JWT провайдер и фильтры
│   └── service/                           — бизнес-логика
└── src/main/resources/
    ├── application.properties             — настройки приложения
    └── templates/                         — Thymeleaf HTML шаблоны
        ├── index.html                     — главная страница
        ├── login.html                     — страница входа
        ├── doctors.html                   — список врачей
        └── patients.html                  — список пациентов
```

---

## ⚙️ Конфигурация

Настройки в `src/main/resources/application.properties`:

```properties
server.port=8081
spring.datasource.url=jdbc:h2:mem:demo2db
spring.h2.console.enabled=true
```

H2 консоль доступна по адресу: `http://localhost:8081/h2-console`

---

## 👤 Автор

**Арон Абзал** — ВТиПО-33

- 🎓 Группа: ВТиПО-33
- 🐙 GitHub: [Abzal05](https://github.com/Abzal05)

---

## 📄 Лицензия

MIT License — свободное использование.

