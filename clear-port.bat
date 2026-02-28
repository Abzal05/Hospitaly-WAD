@echo off
echo ========================================
echo Очистка порта 8080
echo ========================================
echo.

echo Останавливаем все Java процессы...
taskkill /F /IM java.exe 2>nul
if %errorlevel% == 0 (
    echo ✓ Java процессы остановлены
) else (
    echo ✓ Java процессы не найдены
)

echo.
echo Освобождаем порт 8080...

REM Метод 1: через netstat
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING 2^>nul') do (
    echo Убиваем процесс PID %%a...
    taskkill /F /PID %%a >nul 2>&1
)

REM Метод 2: через PowerShell
powershell -Command "Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | ForEach-Object { Write-Host 'Убиваем процесс PID' $_.OwningProcess; Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }"

echo.
echo ========================================
echo ✓ Порт 8080 полностью очищен!
echo ========================================
echo.
timeout /t 3

