@echo off
rem Run project locally: free port, set PORT env var and run bootRun
set PORT_ARG=%1
if "%PORT_ARG%"=="" set PORT_ARG=8081
echo Looking for process listening on port %PORT_ARG% ...
call clear-port.bat %PORT_ARG%
if %ERRORLEVEL% neq 0 (
  echo clear-port.bat failed to free port %PORT_ARG% — trying PowerShell stop-java-on-port.ps1...
  powershell -ExecutionPolicy Bypass -File "%~dp0stop-java-on-port.ps1" -Port %PORT_ARG%
  if %ERRORLEVEL% neq 0 (
    echo Failed to free port %PORT_ARG% using PowerShell script. You may need to run PowerShell as Administrator.
    rem continue anyway
  ) else (
    echo Port %PORT_ARG% freed by PowerShell script.
  )
) else (
  echo Port %PORT_ARG% freed by clear-port.bat.
)

rem Export env vars for Spring Boot / Gradle. Some apps read PORT, others SERVER_PORT.
set PORT=%PORT_ARG%
set SERVER_PORT=%PORT_ARG%
echo Starting Spring Boot with PORT=%PORT% (SERVER_PORT=%SERVER_PORT%) ...

rem Use gradlew in project root. Use --no-daemon to avoid long-running daemon issues in some setups.
call gradlew.bat --no-daemon bootRun
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% neq 0 (
  echo bootRun finished with exit code %EXIT_CODE%.
) else (
  echo bootRun exited successfully.
)
pause
exit /b %EXIT_CODE%
