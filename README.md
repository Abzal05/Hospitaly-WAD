# 🏥 Hospital Management System

Полнофункциональная система управления больницей с REST API и веб-интерфейсом, разработанная на **Spring Boot 4 + H2 Database**.

## Быстрый старт

1. Убедитесь, что установлены Java 17+ и Git.
2. Склонируйте репозиторий (или используйте локальную папку).

```bash
git clone <URL_OF_YOUR_REPO>
cd <REPO_DIR>
```

3. Построить и запустить приложение (Windows):

```powershell
# Очистить порт 8080/8081 (если занят)
.\clear-port.bat 8081

# Собрать
gradlew.bat build -x test

# Запустить
gradlew.bat bootRun
```

Откройте браузер: http://localhost:8081 (или адрес, указанный в логах при старте)

### Порт
По умолчанию приложение использует порт 8081 (см. `src/main/resources/application.properties`). Приложение при старте подберёт свободный порт в диапазоне 8081..8099, если указанный порт занят.

## GitHub: безопасный push с PAT

Чтобы запушить изменения в GitHub безопасно:

1. Не выкладывайте PAT в репозиторий. Удалите токены из кода и истории.
2. Настройте remote и используйте PAT при вводе пароля в prompt, или настройте credential manager.

Пример (один раз):

```bash
git remote add origin https://github.com/Abzal05/Hospital-WAD.git
git branch -M main
git push -u origin main
```

Если требуется push из скрипта, используй GitHub CLI или настроенный credential helper.

## Структура проекта
(см. ранее — осталось без изменений)

## Запуск и отладка в IntelliJ
- Run/Debug кнопки должны работать: убедитесь, что в конфигурации запуска в Program arguments и VM options не указаны конфликтующие -Dserver.port.
- Если порт занят — сначала выполните `clear-port.bat`.

## Утилиты
- `clear-port.bat <port>` — освободит указанный порт (Windows, требует прав администратора для kill). 

---

Если хотите, я подготовлю набор коммитов и дам точную последовательность команд для безопасного пуша (я не буду использовать ваш PAT автоматически).

# Demo2 (Hospital) — краткое руководство

Проект: локальная демо-версия медицинского приложения на Spring Boot.

Быстрый старт

Требования:
- Java 17+
- Gradle (в проекте есть wrapper)

Запуск локально:

1) Освободите порт (если нужно):

Для Windows PowerShell (рекомендуется запускать от имени администратора):

```powershell
powershell -ExecutionPolicy Bypass -File stop-java-on-port.ps1 -Port 8080
```

Или используя bat-скрипт:

```bash
clear-port.bat 8080
```

2) Собрать и запустить:

Windows (через PowerShell или cmd):

```bash
./gradlew.bat clean bootRun
```

Контролы:
- Приложение по умолчанию слушает порт из `server.port` (см. `src/main/resources/application.properties`).
- В проекте используется встроенная H2 база (in-memory) — данные сбрасываются при перезапуске.

Как запушить в GitHub (локально):

```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/ВашПользователь/ВашРепозиторий.git
git push -u origin main
```

ВНИМАНИЕ: не храните персональные токены в репозитории. Для авторизации используйте git credential helper или SSH-ключи.

Если нужен перенос структуры проекта в другой репозиторий — скажите, и я подготовлю инструкции.
