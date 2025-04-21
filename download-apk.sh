#!/bin/bash

# Script to download a pre-built APK from a sample repository

echo "This script will download a pre-built APK of the Calendar Alarm app."
echo "Note: This is a sample script and the URLs are placeholders."
echo ""

# Define the download URL (this is a placeholder)
APK_URL="https://github.com/sample-android-apps/calendar-alarm/releases/download/v1.0/calendar-alarm.apk"
OUTPUT_FILE="calendar-alarm.apk"

# Check if curl or wget is available
if command -v curl &> /dev/null; then
    echo "Downloading APK using curl..."
    curl -L -o "$OUTPUT_FILE" "$APK_URL"
    DOWNLOAD_SUCCESS=$?
elif command -v wget &> /dev/null; then
    echo "Downloading APK using wget..."
    wget -O "$OUTPUT_FILE" "$APK_URL"
    DOWNLOAD_SUCCESS=$?
else
    echo "Error: Neither curl nor wget is installed. Please install one of these tools and try again."
    exit 1
fi

# Check if download was successful
if [ $DOWNLOAD_SUCCESS -eq 0 ]; then
    echo ""
    echo "APK downloaded successfully to: $OUTPUT_FILE"
    echo ""
    echo "To install the APK on your Android device:"
    echo "1. Transfer the APK file to your Android device"
    echo "2. On your Android device, go to Settings > Security"
    echo "3. Enable 'Unknown sources' or 'Install unknown apps' (varies by Android version)"
    echo "4. Open the APK file on your device to install it"
    echo ""
    echo "Note: This is a pre-built APK and does not include your Google Calendar credentials."
    echo "You will need to sign in with your Google account when you first launch the app."
else
    echo ""
    echo "Error: Failed to download the APK. Please check your internet connection and try again."
    echo "Alternatively, you can build the APK yourself using the instructions in the README.md file."
    exit 1
fi
