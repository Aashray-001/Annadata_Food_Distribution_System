@echo off
echo ===================================================
echo Compiling Annadata WEB edition (HTML/CSS/JS front end)
echo ===================================================

if not exist "web\out" mkdir "web\out"

dir /s /b src\model\*.java src\dao\*.java src\db\*.java src\util\PasswordUtil.java web\src\*.java > web\sources-web.txt
javac -cp "lib/*" -d web\out @web\sources-web.txt

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed. Please check errors above.
    pause
    exit /b %errorlevel%
)

echo [SUCCESS] Web edition compiled. Start it with run.bat
pause
