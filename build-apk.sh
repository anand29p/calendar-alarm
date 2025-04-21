#!/bin/bash

# Script to build the Calendar Alarm APK

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java Development Kit (JDK) 11 or higher."
    exit 1
fi

# Check if JAVA_HOME is set
if [ -z "$JAVA_HOME" ]; then
    echo "JAVA_HOME environment variable is not set. Please set it to your JDK installation directory."
    exit 1
fi

echo "Building Calendar Alarm APK..."

# Make gradlew executable
chmod +x gradlew

# Clean the project
./gradlew clean

# Build the release APK
./gradlew assembleRelease

# Check if build was successful
if [ $? -eq 0 ]; then
    APK_PATH="app/build/outputs/apk/release/app-release-unsigned.apk"
    
    if [ -f "$APK_PATH" ]; then
        echo "APK built successfully at: $APK_PATH"
        
        # Optional: Sign the APK if keystore is available
        if [ -f "keystore.jks" ]; then
            echo "Signing the APK..."
            
            # Sign the APK
            jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore keystore.jks \
                      -storepass android -keypass android \
                      "$APK_PATH" android
            
            # Verify the signature
            jarsigner -verify -verbose -certs "$APK_PATH"
            
            # Align the APK
            ZIPALIGN_PATH="$ANDROID_HOME/build-tools/33.0.0/zipalign"
            if [ -f "$ZIPALIGN_PATH" ]; then
                ALIGNED_APK="app/build/outputs/apk/release/app-release-aligned.apk"
                "$ZIPALIGN_PATH" -v 4 "$APK_PATH" "$ALIGNED_APK"
                echo "Aligned APK created at: $ALIGNED_APK"
            else
                echo "zipalign tool not found. Skipping APK alignment."
            fi
        else
            echo "No keystore found. The APK is unsigned and cannot be installed on devices."
            echo "To create a signed APK, you need to create a keystore and sign the APK."
        fi
    else
        echo "APK not found at expected location: $APK_PATH"
        exit 1
    fi
else
    echo "Build failed. Please check the error messages above."
    exit 1
fi
