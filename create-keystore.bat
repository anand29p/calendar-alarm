@echo off
REM Script to create a keystore file for signing Android APKs

echo Creating keystore for signing Android APKs...

REM Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Java is not installed. Please install Java Development Kit (JDK) 11 or higher.
    exit /b 1
)

REM Check if keytool is available
where keytool >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo keytool not found. Please ensure JDK is properly installed.
    exit /b 1
)

REM Default values
set KEYSTORE_FILE=keystore.jks
set ALIAS=android
set KEYSTORE_PASSWORD=android
set KEY_PASSWORD=android
set VALIDITY=10000

REM Check if keystore already exists
if exist "%KEYSTORE_FILE%" (
    echo Keystore file '%KEYSTORE_FILE%' already exists.
    set /p OVERWRITE=Do you want to overwrite it? (y/n): 
    if /i not "%OVERWRITE%"=="y" (
        echo Operation cancelled.
        exit /b 0
    )
)

REM Prompt for keystore information
set /p INPUT=Enter keystore filename [%KEYSTORE_FILE%]: 
if not "%INPUT%"=="" set KEYSTORE_FILE=%INPUT%

set /p INPUT=Enter key alias [%ALIAS%]: 
if not "%INPUT%"=="" set ALIAS=%INPUT%

set /p INPUT=Enter keystore password [%KEYSTORE_PASSWORD%]: 
if not "%INPUT%"=="" set KEYSTORE_PASSWORD=%INPUT%

set /p INPUT=Enter key password [%KEY_PASSWORD%]: 
if not "%INPUT%"=="" set KEY_PASSWORD=%INPUT%

set /p INPUT=Enter validity in days [%VALIDITY%]: 
if not "%INPUT%"=="" set VALIDITY=%INPUT%

REM Create the keystore
echo Generating keystore...
keytool -genkeypair ^
    -keystore "%KEYSTORE_FILE%" ^
    -alias "%ALIAS%" ^
    -keyalg RSA ^
    -keysize 2048 ^
    -validity %VALIDITY% ^
    -storepass "%KEYSTORE_PASSWORD%" ^
    -keypass "%KEY_PASSWORD%" ^
    -dname "CN=Calendar Alarm, OU=Development, O=Calendar Alarm, L=Unknown, ST=Unknown, C=US"

REM Check if keystore was created successfully
if %ERRORLEVEL% EQU 0 (
    echo Keystore created successfully at: %KEYSTORE_FILE%
    echo.
    echo Keystore details:
    echo   File: %KEYSTORE_FILE%
    echo   Alias: %ALIAS%
    echo   Keystore password: %KEYSTORE_PASSWORD%
    echo   Key password: %KEY_PASSWORD%
    echo.
    echo Keep this information secure. You will need it to sign your APKs.
) else (
    echo Failed to create keystore.
    exit /b 1
)

echo.
echo Press any key to exit...
pause >nul
