@echo off
REM Script to download a pre-built APK from a sample repository

echo This script will download a pre-built APK of the Calendar Alarm app.
echo Note: This is a sample script and the URLs are placeholders.
echo.

REM Define the download URL (this is a placeholder)
set APK_URL=https://github.com/sample-android-apps/calendar-alarm/releases/download/v1.0/calendar-alarm.apk
set OUTPUT_FILE=calendar-alarm.apk

REM Check if curl is available (included in Windows 10 1803 and later)
where curl >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Downloading APK using curl...
    curl -L -o "%OUTPUT_FILE%" "%APK_URL%"
    set DOWNLOAD_SUCCESS=%ERRORLEVEL%
) else (
    REM Try using PowerShell if curl is not available
    where powershell >nul 2>nul
    if %ERRORLEVEL% EQU 0 (
        echo Downloading APK using PowerShell...
        powershell -Command "(New-Object Net.WebClient).DownloadFile('%APK_URL%', '%OUTPUT_FILE%')"
        set DOWNLOAD_SUCCESS=%ERRORLEVEL%
    ) else (
        echo Error: Neither curl nor PowerShell is available. Please install curl and try again.
        exit /b 1
    )
)

REM Check if download was successful
if %DOWNLOAD_SUCCESS% EQU 0 (
    echo.
    echo APK downloaded successfully to: %OUTPUT_FILE%
    echo.
    echo To install the APK on your Android device:
    echo 1. Transfer the APK file to your Android device
    echo 2. On your Android device, go to Settings ^> Security
    echo 3. Enable 'Unknown sources' or 'Install unknown apps' (varies by Android version)
    echo 4. Open the APK file on your device to install it
    echo.
    echo Note: This is a pre-built APK and does not include your Google Calendar credentials.
    echo You will need to sign in with your Google account when you first launch the app.
) else (
    echo.
    echo Error: Failed to download the APK. Please check your internet connection and try again.
    echo Alternatively, you can build the APK yourself using the instructions in the README.md file.
    exit /b 1
)

echo.
echo Press any key to exit...
pause >nul
