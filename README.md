# 🏥 Hospital Management System

Полнофункциональная система управления больницей с REST API и веб-интерфейсом, разработанная на **Spring Boot 4 + H2 Database**.

## ✨ Возможности

| Модуль | Функциональность |
|--------|-----------------|
| 👤 **Пользователи** | Регистрация, вход, JWT-авторизация |
| 🏥 **Больницы** | Управление больницами, отделениями |
| 👨‍⚕️ **Доктора** | Добавление врачей, специальности, расписание |
| 🤒 **Пациенты** | Регистрация пациентов, история болезни |
| 📅 **Приёмы** | Запись на приёмы, управление расписанием |
| 🔐 **Безопасность** | JWT токены, ролевая система доступа |

## 🛠 Технологии

- **Backend**: Spring Boot 4.0.3, Spring Data JPA, Hibernate 7
- **Database**: H2 (в памяти, для разработки)
- **Security**: JWT, Spring Security, BCrypt
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **Build**: Gradle
- **Java**: 17+

## 🚀 Быстрый старт

### Минимальные требования
- Java JDK 17+
- Git

### Установка и запуск

```bash
# 1. Клонировать репозиторий
git clone https://github.com/Abzal05/Hospital-WAD.git
cd Hospital-WAD

# 2. Собрать проект
./gradlew build -x test  # На Linux/Mac
# или
gradlew.bat build -x test  # На Windows

# 3. Запустить приложение
./gradlew bootRun  # На Linux/Mac
# или
gradlew.bat bootRun  # На Windows
```

**Откройте браузер**: http://localhost:8080

### Быстрый запуск через PowerShell (Windows)

```powershell
# На Windows запустите скрипт
.\start.ps1
```

### Запуск через Batch скрипт (Windows)

```batch
# Просто дважды кликните на
run.bat
```

### Запуск в IntelliJ IDEA

1. Откройте проект в IntelliJ IDEA
2. Нажмите на зелёную кнопку **Run** (или Shift+F10)
3. Приложение откроется на http://localhost:8080
4. Логин: **admin**, Пароль: **admin**

## 🔌 REST API

### Base URL
```
http://localhost:8080/api
```

### Все защищённые эндпоинты требуют заголовок:
```
Authorization: Bearer <JWT_TOKEN>
```

### 🔐 Аутентификация `/auth`

| Метод | URL | Описание | Auth |
|-------|-----|---------|------|
| POST | `/auth/register` | Регистрация | ❌ |
| POST | `/auth/login` | Вход | ❌ |

**Регистрация:**
```json
POST /auth/register
{
  "username": "doctor1",
  "password": "password123",
  "email": "doctor@hospital.com"
}
```

**Вход:**
```json
POST /auth/login
{
  "username": "doctor1",
  "password": "password123"
}
```

### 🏥 Больницы `/hospitals`

| Метод | URL | Описание | Auth |
|-------|-----|---------|------|
| GET | `/hospitals` | Получить все больницы | ✅ |
| POST | `/hospitals` | Создать больницу | ✅ |
| GET | `/hospitals/{id}` | Получить больницу | ✅ |

### 👨‍⚕️ Доктора `/doctors`

| Метод | URL | Описание | Auth |
|-------|-----|---------|------|
| GET | `/doctors` | Получить всех докторов | ✅ |
| POST | `/doctors` | Добавить доктора | ✅ |
| GET | `/doctors/{id}` | Получить доктора | ✅ |

### 🤒 Пациенты `/patients`

| Метод | URL | Описание | Auth |
|-------|-----|---------|------|
| GET | `/patients` | Получить всех пациентов | ✅ |
| POST | `/patients` | Добавить пациента | ✅ |
| GET | `/patients/{id}` | Получить пациента | ✅ |

### 📅 Приёмы `/appointments`

| Метод | URL | Описание | Auth |
|-------|-----|---------|------|
| GET | `/appointments` | Получить приёмы | ✅ |
| POST | `/appointments` | Записать приём | ✅ |
| PUT | `/appointments/{id}` | Обновить приём | ✅ |
| DELETE | `/appointments/{id}` | Отменить приём | ✅ |

## 📁 Структура проекта

```
hospital-system/
├── src/main/java/com/example/demo/
│   ├── Demo2Application.java           — Главный класс
│   ├── config/                         — Конфигурация (Security, Data)
│   ├── controller/
│   │   ├── api/                        — REST контроллеры
│   │   └── mvc/                        — MVC контроллеры для веб-интерфейса
│   ├── dto/                            — DTO объекты
│   ├── model/                          — JPA сущности
│   ├── repository/                     — Spring Data репозитории
│   ├── security/                       — Компоненты безопасности (JWT)
│   └── service/                        — Бизнес-логика
├── src/main/resources/
│   ├── application.properties          — Конфигурация приложения
│   ├── static/
│   │   ├── css/style.css               — Стили
│   │   └── js/app.js                   — JavaScript скрипты
│   └── templates/
│       ├── index.html                  — Главная страница
│       ├── login.html                  — Страница входа
│       ├── doctors.html                — Список докторов
│       └── patients.html               — Список пациентов
├── build.gradle                        — Конфигурация Gradle
├── README.md                           — Этот файл
└── .gitignore                          — Git ignore

```

## 📝 Типичный сценарий работы

1. Открыть http://localhost:8080
2. Зарегистрироваться или войти
3. Управлять больницами, докторами, пациентами и приёмами через веб-интерфейс
4. Использовать REST API через Postman или любой HTTP клиент

## 🔑 Тестовые учётные данные

После первого запуска в базе есть тестовые данные:
- **Пользователь**: admin / admin
- **Больница**: City Hospital
- **Доктора**: несколько врачей разных специальностей

## ⚙️ Конфигурация

Основные параметры в `src/main/resources/application.properties`:

```properties
spring.application.name=demo2
spring.jpa.hibernate.ddl-auto=create-drop
spring.datasource.url=jdbc:h2:mem:demo2db
server.port=8080
```

## 🐛 Решение проблем

### Порт 8080 уже занят
```powershell
# На Windows
Get-Process java | Stop-Process -Force
```

### Ошибка при сборке
```bash
# Очистить кэш Gradle
./gradlew clean
./gradlew build -x test
```

### Приложение не запускается
1. Проверьте Java 17+: `java -version`
2. Проверьте настройки firewall
3. Посмотрите логи ошибок

## 📄 Лицензия

MIT License — свободное использование в образовательных целях.

## 👤 Автор

**Макеш Найман** — Группа ВТиПО-33

- GitHub: [@Abzal05](https://github.com/Abzal05)
- Репозиторий: [Hospital-WAD](https://github.com/Abzal05/Hospital-WAD)
- Email: makeshnaiman@gmail.com

---

**Последнее обновление**: 2026-02-28

