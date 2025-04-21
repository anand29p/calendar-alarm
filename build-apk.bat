@echo off
REM Script to build the Calendar Alarm APK on Windows

REM Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Java is not installed. Please install Java Development Kit (JDK) 11 or higher.
    exit /b 1
)

REM Check if JAVA_HOME is set
if "%JAVA_HOME%" == "" (
    echo JAVA_HOME environment variable is not set. Please set it to your JDK installation directory.
    exit /b 1
)

echo Building Calendar Alarm APK...

REM Build the release APK
call gradlew.bat clean assembleRelease

REM Check if build was successful
if %ERRORLEVEL% EQU 0 (
    set APK_PATH=app\build\outputs\apk\release\app-release-unsigned.apk
    
    if exist "%APK_PATH%" (
        echo APK built successfully at: %APK_PATH%
        
        REM Optional: Sign the APK if keystore is available
        if exist "keystore.jks" (
            echo Signing the APK...
            
            REM Sign the APK
            "%JAVA_HOME%\bin\jarsigner" -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore keystore.jks ^
                      -storepass android -keypass android ^
                      "%APK_PATH%" android
            
            REM Verify the signature
            "%JAVA_HOME%\bin\jarsigner" -verify -verbose -certs "%APK_PATH%"
            
            REM Align the APK
            set ZIPALIGN_PATH=%ANDROID_HOME%\build-tools\33.0.0\zipalign.exe
            if exist "%ZIPALIGN_PATH%" (
                set ALIGNED_APK=app\build\outputs\apk\release\app-release-aligned.apk
                "%ZIPALIGN_PATH%" -v 4 "%APK_PATH%" "%ALIGNED_APK%"
                echo Aligned APK created at: %ALIGNED_APK%
            ) else (
                echo zipalign tool not found. Skipping APK alignment.
            )
        ) else (
            echo No keystore found. The APK is unsigned and cannot be installed on devices.
            echo To create a signed APK, you need to create a keystore and sign the APK.
        )
    ) else (
        echo APK not found at expected location: %APK_PATH%
        exit /b 1
    )
) else (
    echo Build failed. Please check the error messages above.
    exit /b 1
)

echo.
echo Build process completed.
