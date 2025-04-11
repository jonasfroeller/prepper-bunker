for /f "tokens=5" %%a in ('netstat -ano ^| findstr :9092') do taskkill /PID %%a /F

REM Stop and remove h2 container if it exists
:: docker stop h2 2>nul
:: docker rm h2 2>nul

REM Run the h2 container
docker run -p 9092:9092 --rm --name h2 aisge/h2
pause
