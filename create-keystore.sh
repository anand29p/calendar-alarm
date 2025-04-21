#!/bin/bash

# Script to create a keystore file for signing Android APKs

echo "Creating keystore for signing Android APKs..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java Development Kit (JDK) 11 or higher."
    exit 1
fi

# Check if keytool is available
if ! command -v keytool &> /dev/null; then
    echo "keytool not found. Please ensure JDK is properly installed."
    exit 1
fi

# Default values
KEYSTORE_FILE="keystore.jks"
ALIAS="android"
KEYSTORE_PASSWORD="android"
KEY_PASSWORD="android"
VALIDITY=10000 # Validity in days (about 27 years)

# Check if keystore already exists
if [ -f "$KEYSTORE_FILE" ]; then
    echo "Keystore file '$KEYSTORE_FILE' already exists."
    read -p "Do you want to overwrite it? (y/n): " OVERWRITE
    if [ "$OVERWRITE" != "y" ]; then
        echo "Operation cancelled."
        exit 0
    fi
fi

# Prompt for keystore information
read -p "Enter keystore filename [$KEYSTORE_FILE]: " INPUT
KEYSTORE_FILE=${INPUT:-$KEYSTORE_FILE}

read -p "Enter key alias [$ALIAS]: " INPUT
ALIAS=${INPUT:-$ALIAS}

read -p "Enter keystore password [$KEYSTORE_PASSWORD]: " INPUT
KEYSTORE_PASSWORD=${INPUT:-$KEYSTORE_PASSWORD}

read -p "Enter key password [$KEY_PASSWORD]: " INPUT
KEY_PASSWORD=${INPUT:-$KEY_PASSWORD}

read -p "Enter validity in days [$VALIDITY]: " INPUT
VALIDITY=${INPUT:-$VALIDITY}

# Create the keystore
echo "Generating keystore..."
keytool -genkeypair \
    -keystore "$KEYSTORE_FILE" \
    -alias "$ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity "$VALIDITY" \
    -storepass "$KEYSTORE_PASSWORD" \
    -keypass "$KEY_PASSWORD" \
    -dname "CN=Calendar Alarm, OU=Development, O=Calendar Alarm, L=Unknown, ST=Unknown, C=US"

# Check if keystore was created successfully
if [ $? -eq 0 ]; then
    echo "Keystore created successfully at: $KEYSTORE_FILE"
    echo ""
    echo "Keystore details:"
    echo "  File: $KEYSTORE_FILE"
    echo "  Alias: $ALIAS"
    echo "  Keystore password: $KEYSTORE_PASSWORD"
    echo "  Key password: $KEY_PASSWORD"
    echo ""
    echo "Keep this information secure. You will need it to sign your APKs."
else
    echo "Failed to create keystore."
    exit 1
fi
