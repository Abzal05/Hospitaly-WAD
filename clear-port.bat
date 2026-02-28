@echo off
rem Скрипт для освобождения указанного порта (по умолчанию 8080)
setlocal enabledelayedexpansion
set PORT=%1
if "%PORT%"=="" set PORT=8080
echo Looking for process listening on port %PORT% ...
set PID=
for /f "tokens=5" %%a in ('netstat -ano ^| findstr /R /C:":%PORT% .*LISTEN"') do (
    set PID=%%a
)
if not defined PID (
    echo No process found listening on port %PORT%.
    exit /b 0
)
echo Found PID=!PID! using port %PORT%.
echo Attempting to kill PID !PID! ...
taskkill /F /PID !PID! >nul 2>&1
if %ERRORLEVEL%==0 (
    echo Successfully killed process !PID!. Port %PORT% freed.
    exit /b 0
) else (
    echo Failed to kill process !PID!. Try running this script as Administrator or use the PowerShell script stop-java-on-port.ps1.
    exit /b 1
)
