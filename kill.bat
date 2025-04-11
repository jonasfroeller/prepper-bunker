@echo off
REM Kill processes on specific ports
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8069') do taskkill /PID %%a /F
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :4200') do taskkill /PID %%a /F
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :9092') do taskkill /PID %%a /F

REM Stop and remove h2 container if it exists
docker stop h2 2>nul
docker rm h2 2>nul
