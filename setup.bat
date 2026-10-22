@echo off
setlocal
echo ===================================================
echo Annadata - one-click setup (check, import DB, compile)
echo ===================================================

if "%DB_USER%"=="" set DB_USER=root
if "%DB_PASSWORD%"=="" set DB_PASSWORD=root

where javac >nul 2>nul
if errorlevel 1 (
    echo [ERROR] JDK 11+ not found on PATH. Install a JDK and reopen this window.
    pause & exit /b 1
)

set MYSQL=mysql
where mysql >nul 2>nul
if errorlevel 1 (
    set MYSQL="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
)
if not exist %MYSQL% if "%MYSQL%"=="" (
    echo [ERROR] mysql client not found. Install MySQL 8 or add it to PATH.
    pause & exit /b 1
)

echo Creating database and importing dumps...
%MYSQL% -u %DB_USER% -p%DB_PASSWORD% -e "CREATE DATABASE IF NOT EXISTS food_donation_db;"
if errorlevel 1 (
    echo [ERROR] Cannot connect to MySQL. Make sure the server is running on 3306 and the credentials are right.
    pause & exit /b 1
)
for %%T in (users distribution_places donations requests emergency_aids notifications distribution_schedule routines) do (
    echo   importing %%T
    %MYSQL% -u %DB_USER% -p%DB_PASSWORD% food_donation_db < sql\dumps\food_donation_db_%%T.sql
)

echo Compiling web edition...
call compile.bat
if errorlevel 1 exit /b 1

echo.
echo [SUCCESS] Setup complete. Start the app with run.bat, then open http://localhost:8080
echo Login: admin@foodshare.com / admin123
pause
