# Скрипт запуска Hospital Management System
Write-Host "=== Hospital Management System ===" -ForegroundColor Green
Write-Host "Версия: 1.0.0" -ForegroundColor Cyan

# Проверка Java
Write-Host "Проверяем Java..." -ForegroundColor Yellow
$javaExe = "C:\Users\Naiman\.jdks\ms-17.0.17\bin\java.exe"
$javaVersion = & $javaExe -version 2>&1
Write-Host $javaVersion -ForegroundColor Gray

# Освобождаем порт 8080
Write-Host "Освобождаем порт 8080..." -ForegroundColor Yellow
$port = 8080

# Убиваем все java процессы
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep 1

# Убиваем процессы на порту 8080
Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue | ForEach-Object {
    Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue
}
Start-Sleep 2
Write-Host "✅ Порт $port очищен" -ForegroundColor Green

# Собираем проект
Write-Host "Сборка проекта..." -ForegroundColor Cyan
Set-Location "C:\Users\Naiman\IdeaProjects\demo2"
$env:JAVA_HOME = "C:\Users\Naiman\.jdks\ms-17.0.17"

& ".\gradlew.bat" clean build -x test

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Ошибка сборки!" -ForegroundColor Red
    Read-Host "Нажмите Enter для выхода"
    exit 1
}

Write-Host "✅ Сборка успешна" -ForegroundColor Green

# Запускаем приложение
Write-Host "`nЗапуск приложения..." -ForegroundColor Green
Write-Host "URL: http://localhost:8080" -ForegroundColor Cyan
Write-Host "Логин: admin, Пароль: admin" -ForegroundColor Cyan
Write-Host "Нажмите Ctrl+C для остановки`n" -ForegroundColor Yellow

& $javaExe -jar "build\libs\demo2-0.0.1-SNAPSHOT.jar"

