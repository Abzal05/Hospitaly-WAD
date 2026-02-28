# 🚀 БЫСТРЫЙ СТАРТ

## Windows - Самый быстрый способ

### Способ 1: Batch файл (Рекомендуется)
1. **Дважды кликните** на файл `run.bat` в корне проекта
2. Дождитесь сборки (30 секунд)
3. Откройте браузер: http://localhost:8080
4. Логин: `admin` / Пароль: `admin`

### Способ 2: PowerShell скрипт
```powershell
.\start.ps1
```

### Способ 3: Gradle command
```bash
./gradlew bootRun
```

### Способ 4: IntelliJ IDEA
1. Откройте проект
2. Нажмите Shift+F10 (или зелёную кнопку Run)
3. Выберите `Demo2Application` → Run
4. Готово! http://localhost:8080

## Linux/Mac

```bash
# Сборка проекта
./gradlew clean build -x test

# Запуск
java -jar build/libs/demo2-0.0.1-SNAPSHOT.jar

# Или через Gradle
./gradlew bootRun
```

## 🔧 Если не работает

### Порт 8080 занят
```powershell
# Windows
Get-Process java | Stop-Process -Force

# Linux/Mac
killall java
```

### Очистить Gradle кэш
```bash
./gradlew clean
```

### Полная пересборка
```bash
./gradlew clean build -x test
```

## 📍 URL и Учётные данные

| Параметр | Значение |
|----------|----------|
| **URL** | http://localhost:8080 |
| **Логин** | admin |
| **Пароль** | admin |
| **API Base** | http://localhost:8080/api |
| **H2 Console** | http://localhost:8080/h2-console |
| **GitHub** | https://github.com/Abzal05/Hospital-WAD |

## 🔑 H2 Database Console

Если нужно посмотреть БД:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:demo2db`
- Username: `sa`
- Password: (пусто)

## 📝 Типичные операции

### Создать нового пациента
```bash
curl -X POST http://localhost:8080/api/patients \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Иван",
    "lastName": "Петров",
    "email": "ivan@example.com",
    "phone": "+7 999 123 45 67"
  }'
```

### Получить список докторов
```bash
curl -X GET http://localhost:8080/api/doctors \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 🆘 Проблемы и решения

| Проблема | Решение |
|----------|---------|
| Приложение не запускается | Проверьте Java 17+: `java -version` |
| Порт 8080 занят | `taskkill /F /IM java.exe` |
| Ошибка сборки | `./gradlew clean build -x test` |
| Базу нельзя подключить | Перезагрузите приложение, БД в памяти |

---

**Нужна помощь?** Смотрите README.md

