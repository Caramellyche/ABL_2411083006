@echo off
setlocal
cd /d "%~dp0"

if not defined JWT_SECRET (
    echo ERROR: Set JWT_SECRET before starting services. See README.md.
    exit /b 1
)

echo Checking required application ports...
set "PORT_CONFLICT="
call :check_port 8761
call :check_port 8080
call :check_port 8081
call :check_port 8082
call :check_port 8083
call :check_port 8084
call :check_port 8085
call :check_port 8086
if defined PORT_CONFLICT (
    echo.
    echo ERROR: Port conflict found. No services were started.
    exit /b 1
)

echo Checking RabbitMQ on port 5672...
sc.exe start RabbitMQ >nul 2>&1
set /a WAIT_COUNT=0
:wait_rabbitmq
powershell.exe -NoProfile -Command "$c=Get-NetTCPConnection -State Listen -LocalPort 5672 -ErrorAction SilentlyContinue; if($c){exit 0}else{exit 1}" >nul 2>&1
if not errorlevel 1 goto rabbitmq_ready
set /a WAIT_COUNT+=1
if %WAIT_COUNT% GEQ 20 (
    echo ERROR: RabbitMQ did not start listening on port 5672. No application services were started.
    exit /b 1
)
ping -n 2 127.0.0.1 >nul
goto wait_rabbitmq

:rabbitmq_ready
echo RabbitMQ is listening on port 5672.

call :start_service "Eureka" "test"
echo Menunggu inisialisasi Eureka (3 detik)...
ping -n 4 127.0.0.1 >nul

call :start_service "Product" "product"
call :start_service "Customer" "customer"
call :start_service "Order Command" "order_command"
call :start_service "Order Query" "order_query"
call :start_service "API Gateway" "api-gateway"
call :start_service "Auth Service" "auth-service"
start "Email Service" /D "%~dp0email-service" powershell.exe -NoExit -NoProfile -ExecutionPolicy Bypass -Command "$email = Read-Host 'Masukkan email Gmail'; $secure = Read-Host 'Masukkan App Password Gmail' -AsSecureString; $ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure); try { $env:MAIL_USERNAME = $email; $env:MAIL_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr); .\mvnw.cmd spring-boot:run } finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr); Remove-Item Env:MAIL_PASSWORD -ErrorAction SilentlyContinue }"

echo.
echo Services launched in separate windows.
echo Email Service window will prompt for Gmail address and App Password.
exit /b 0

:check_port
for /f "tokens=5" %%P in ('netstat -ano -p tcp ^| findstr /R /C:":%~1 .*LISTENING"') do (
    echo Port %~1 is already in use by PID %%P.
    set "PORT_CONFLICT=1"
)
exit /b 0

:start_service
echo Starting %~1...
start "%~1" /D "%~dp0%~2" cmd /k "mvnw.cmd spring-boot:run"
exit /b 0
