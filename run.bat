@echo off
chcp 65001 >nul
cls
echo.
echo ========================================
echo Hospital Management System
echo ========================================
echo.

setlocal enabledelayedexpansion

REM Остановить все java процессы
echo Остановляем предыдущие процессы...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 2 /nobreak

REM Сборка проекта
echo.
echo Сборка проекта...
call gradlew.bat clean build -x test
if errorlevel 1 (
    echo Ошибка сборки!
    pause
    exit /b 1
)

echo.
echo Запуск приложения...
echo ========================================
echo URL: http://localhost:8080
echo Логин: admin
echo Пароль: admin
echo ========================================
echo.

REM Запуск приложения
set JAVA_HOME=C:\Users\Naiman\.jdks\ms-17.0.17
"%JAVA_HOME%\bin\java.exe" -jar "build\libs\demo2-0.0.1-SNAPSHOT.jar"

pause

