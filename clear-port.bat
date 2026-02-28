@echo off
rem Скрипт для освобождения порта 8080/8081 если нужно
set PORT=%1
if "%PORT%"=="" set PORT=8080

echo Attempting to find process using port %PORT% ...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%PORT% " ^| findstr LISTENING') do (
    set PID=%%a
)

if not defined PID (
    echo No process found listening on port %PORT%.
    exit /b 0
)

echo Killing PID %PID% ...
taskkill /PID %PID% /F
if %ERRORLEVEL%==0 (
    echo Port %PORT% freed.
) else (
    echo Failed to kill process %PID%. You may need to run as Administrator.
)

