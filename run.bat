@echo off
chcp 65001 >nul
cls
echo.
echo ========================================
echo Hospital Management System
echo ========================================
echo.

setlocal enabledelayedexpansion

set PORT=8081
if not "%1"=="" set PORT=%1

REM Остановить процессы, слушающие порт
call clear-port.bat %PORT%

REM Сборка проекта
echo.
echo Сборка проекта...
call gradlew.bat clean build -x test
if errorlevel 1 (
    echo Ошибка сборки!
    pause
    exit /b 1
)

REM Найти jar
set JARPATH=build\libs\demo2-0.0.1-SNAPSHOT.jar
if not exist "%JARPATH%" (
    echo Не найден JAR: %JARPATH%
    pause
    exit /b 1
)

echo.
echo Запуск приложения на порту %PORT%...
set JAVA_HOME=C:\Users\Naiman\.jdks\ms-17.0.17
"%JAVA_HOME%\bin\java.exe" -Dserver.port=%PORT% -jar "%JARPATH%"

pause
