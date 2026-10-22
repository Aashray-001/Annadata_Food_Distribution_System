@echo off
echo ===================================================
echo Starting Annadata WEB edition
echo ===================================================

rem Optional custom settings (defaults: root / root, port 8080)
rem set DB_URL=jdbc:mysql://localhost:3306/food_donation_db
rem set DB_USER=root
rem set DB_PASSWORD=root
rem set PORT=8080

if not exist "web\out\web\WebServer.class" (
    echo Web edition is not compiled yet - compiling first...
    call compile.bat
)

echo Open http://localhost:8080 in your browser. Press Ctrl+C to stop.
java -cp "web\out;lib\*" web.WebServer

if %errorlevel% neq 0 (
    echo.
    echo [NOTE] If the server failed to start:
    echo 1. Make sure MySQL is running on port 3306 and the database was imported ^(setup.bat^).
    echo 2. Check that nothing else is using port 8080 ^(set PORT=8081 to change it^).
    pause
)
