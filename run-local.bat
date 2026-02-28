@echo off
rem Run project locally: free port, set PORT env var and run bootRun
set PORT_ARG=%1
if "%PORT_ARG%"=="" set PORT_ARG=8081
call clear-port.bat %PORT_ARG%
if %ERRORLEVEL% neq 0 (
  echo Failed to free port %PORT_ARG%. Continue anyway?
)
set PORT=%PORT_ARG%
echo Starting Spring Boot with PORT=%PORT% ...
call gradlew.bat bootRun
pause

