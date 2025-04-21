# Calendar Alarm App

An Android application that integrates with Google Calendar and provides alarm notifications for upcoming events.

## Features

- Google Calendar integration
- Notification alerts 10 minutes before events and at event start time
- Alarm sound and vibration for notifications
- Background syncing of calendar events
- Works with device reboot
- Material Design UI

## Setup Instructions

### Prerequisites

- Android Studio Arctic Fox (2021.3.1) or newer
- Android SDK 33 (Android 13) or newer
- Google Play Services SDK
- A Google Cloud Platform project with the Google Calendar API enabled

### Google Cloud Platform Setup

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google Calendar API
4. Create OAuth 2.0 credentials for an Android app
5. Add your app's package name and SHA-1 signing certificate to the credentials
6. Download the `google-services.json` file and place it in the `app/` directory

## Building the App

### Option 1: Using Android Studio

1. Clone this repository
2. Open the project in Android Studio
3. Replace the placeholder `google-services.json` with your own file from Google Cloud Console
4. Build and run the app on your device or emulator

### Option 2: Building APK from Command Line

#### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Android SDK with build tools

#### On Linux/macOS:
1. Make the build script executable:
   ```
   chmod +x build-apk.sh
   ```
2. Run the build script:
   ```
   ./build-apk.sh
   ```

#### On Windows:
1. Run the build script:
   ```
   build-apk.bat
   ```

The unsigned APK will be generated at `app/build/outputs/apk/release/app-release-unsigned.apk`.

#### Creating a Keystore

We've included scripts to help you create a keystore file:

On Linux/macOS:
```
chmod +x create-keystore.sh
./create-keystore.sh
```

On Windows:
```
create-keystore.bat
```

Follow the prompts to create your keystore. The default values will create a keystore file named `keystore.jks` with the alias `android` and password `android`.

### Option 3: Using GitHub Actions (No Local Resources Required)

If you don't have a computer with enough resources to install Java and the Android SDK, you can use GitHub Actions to build the APK:

1. Fork this repository to your GitHub account
2. GitHub Actions will automatically build the APK when you push changes
3. Download the APK from the Actions tab in your GitHub repository

The workflow file is already included at `.github/workflows/android.yml`.

We've included scripts to help you push this project to GitHub:

On Linux/macOS:
```
chmod +x push-to-github.sh
./push-to-github.sh
```

On Windows:
```
push-to-github.bat
```

These scripts require the GitHub CLI (`gh`) to be installed. They will:
1. Create a new GitHub repository
2. Push all the code to the repository
3. Provide instructions on how to download the APK

**New to GitHub CLI?** Check out our [GitHub CLI Guide](GITHUB_CLI_GUIDE.md) for detailed instructions on how to verify installation, log in, and use GitHub CLI commands.

For more alternative build options, see [ALTERNATIVE_BUILD_OPTIONS.md](ALTERNATIVE_BUILD_OPTIONS.md).

**Looking for installation links?** Check out [INSTALLATION_LINKS.md](INSTALLATION_LINKS.md) for direct links to all resources needed to install the app.

## Usage

1. Launch the app and sign in with your Google account
2. Grant the necessary permissions for calendar access and notifications
3. The app will sync your calendar events and schedule notifications
4. You will receive alarm notifications 10 minutes before each event and at the event start time
5. Pull down to refresh and sync the latest events

## Permissions

The app requires the following permissions:

- `READ_CALENDAR`: To access calendar events on the device
- `INTERNET`: To connect to Google Calendar API
- `POST_NOTIFICATIONS`: To show notifications (Android 13+)
- `VIBRATE`: To vibrate the device for notifications
- `RECEIVE_BOOT_COMPLETED`: To reschedule notifications after device reboot
- `SCHEDULE_EXACT_ALARM`: To schedule precise alarm notifications

## Architecture

The app follows the MVVM (Model-View-ViewModel) architecture pattern and uses the following components:

- **Repository Pattern**: For data access and management
- **WorkManager**: For background tasks and scheduling notifications
- **LiveData & Coroutines**: For asynchronous operations and UI updates
- **View Binding**: For type-safe view access
- **Material Components**: For UI elements

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Google Calendar API
- Android Jetpack libraries
- Material Design components
