@echo off
chcp 65001 >nul
cls
echo ========================================
echo Подготовка репозитория для пуша
echo ========================================

echo Проверяем папку docs...
if not exist docs\general (
    echo Создаём docs\general...
    mkdir docs\general
)

echo Список файлов в docs:
for %%f in (docs\*) do echo %%~nxf

echo.
echo Следующие файлы будут перемещены в docs\general или удалены:
if exist "docs\БЫСТРЫЙ_СТАРТ.txt" echo docs\БЫСТРЫЙ_СТАРТ.txt
if exist "docs\ИНСТРУКЦИЯ.md" echo docs\ИНСТРУКЦИЯ.md
if exist "docs\ИСПРАВЛЕНИЕ_РЕГИСТРАЦИИ.txt" echo docs\ИСПРАВЛЕНИЕ_РЕГИСТРАЦИИ.txt

echo.
set /p CONFIRM=Продолжить очистку и перемещение файлов? (y/N):
if /I not "%CONFIRM%"=="y" (
    echo Операция отменена пользователем.
    exit /b 0
)

if exist "docs\БЫСТРЫЙ_СТАРТ.txt" (
    move /Y "docs\БЫСТРЫЙ_СТАРТ.txt" "docs\general\"
)
if exist "docs\ИНСТРУКЦИЯ.md" (
    move /Y "docs\ИНСТРУКЦИЯ.md" "docs\general\"
)
if exist "docs\ИСПРАВЛЕНИЕ_РЕГИСТРАЦИИ.txt" (
    move /Y "docs\ИСПРАВЛЕНИЕ_РЕГИСТРАЦИИ.txt" "docs\general\"
)

echo Удаляем лишние файлы в корне docs (если остались)...
for %%f in (docs\*) do (
    if /I not "%%~nxf"=="general" (
        rem если это не папка general — пропустим (файлы уже перемещены)
        echo Пропускаем: %%~nxf
    )
)

echo Готово. Проверьте изменения, затем выполните git add/commit/push по инструкции в docs\GIT_PUSH.md
pause

