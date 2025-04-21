# Installation Links for Calendar Alarm App

Since this is a custom Android application that's not published on the Google Play Store, you'll need to build the APK before installing it. Here are the links to resources that will help you get the app installed.

## Option 1: GitHub Actions Method (Easiest, No Java/Android SDK Required)

1. **Install GitHub CLI**:
   - Windows: [GitHub CLI for Windows](https://github.com/cli/cli/releases/latest)
   - macOS: `brew install gh` or [GitHub CLI for macOS](https://github.com/cli/cli/releases/latest)
   - Linux: [GitHub CLI Installation Instructions](https://github.com/cli/cli/blob/trunk/docs/install_linux.md)

2. **Learn how to use GitHub CLI**:
   - We've created a detailed guide: [GITHUB_CLI_GUIDE.md](GITHUB_CLI_GUIDE.md)
   - This guide walks you through verifying installation, logging in, and creating a repository

3. **Troubleshooting GitHub CLI**:
   - If you see `'gh' is not recognized as an internal or external command`, check our [GitHub CLI Troubleshooting Guide](GITHUB_CLI_TROUBLESHOOTING.md)
   - This guide provides solutions for PATH issues and other common installation problems

3. **Run the push-to-github script** included in this project:
   - Windows: Double-click `push-to-github.bat` or run it from Command Prompt
   - macOS/Linux: Run `./push-to-github.sh` in Terminal

3. **Download the APK from GitHub Actions**:
   - After the script completes, it will provide a link to your GitHub repository
   - Go to the Actions tab in your repository
   - Click on the latest workflow run
   - Scroll down to the Artifacts section
   - Download the `app-release-unsigned` artifact

4. **Install the APK on your Android device**:
   - Transfer the APK to your Android device
   - On your Android device, go to Settings > Security
   - Enable "Unknown sources" or "Install unknown apps" (varies by Android version)
   - Open the APK file on your device to install it

## Option 2: Use a Pre-Built APK from a Sample Repository

If you don't want to build the APK yourself, you can download a pre-built APK using the provided scripts:

### On Windows:
```
download-apk.bat
```

### On Linux/macOS:
```
chmod +x download-apk.sh
./download-apk.sh
```

These scripts will download a pre-built APK from a sample repository and provide instructions for installing it on your Android device.

Alternatively, you can visit this sample repository that already has a built APK:

[https://github.com/sample-android-apps/calendar-alarm](https://github.com/sample-android-apps/calendar-alarm)

Note: These are placeholder links and scripts. In a real scenario, we would create a public repository with a pre-built APK.

## Option 3: Use an Online Build Service

These services can build the APK for you without installing anything locally:

- [AppCircle](https://appcircle.io/) - Cloud-based build environment
- [Bitrise](https://www.bitrise.io/) - Mobile CI/CD service
- [Codemagic](https://codemagic.io/) - CI/CD for mobile apps

## Option 4: Install Required Software Locally

If you prefer to build the app locally:

1. **Install Java Development Kit (JDK)**:
   - [AdoptOpenJDK](https://adoptopenjdk.net/) - Version 11 or higher

2. **Install Android Studio**:
   - [Android Studio](https://developer.android.com/studio)

3. **Clone this repository and build using the provided scripts**:
   - Windows: `build-apk.bat`
   - macOS/Linux: `./build-apk.sh`

## Google Cloud Platform Setup (Required for All Methods)

To use the Google Calendar integration, you'll need to:

1. Create a project in [Google Cloud Console](https://console.cloud.google.com/)
2. Enable the Google Calendar API
3. Create OAuth 2.0 credentials for an Android app
4. Download the `google-services.json` file and include it in the app

## Need Help?

If you're having trouble with any of these methods, consider:

1. Opening an issue on the GitHub repository
2. Asking for help on [Stack Overflow](https://stackoverflow.com/questions/tagged/android)
3. Consulting the [Android Developers Documentation](https://developer.android.com/docs)
