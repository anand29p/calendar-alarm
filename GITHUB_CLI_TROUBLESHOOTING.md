# GitHub CLI Troubleshooting Guide

If you're seeing the error `'gh' is not recognized as an internal or external command`, it means that GitHub CLI is either not installed correctly or not in your system PATH. Here's how to fix it:

## Windows Troubleshooting

### Option 1: Reinstall GitHub CLI with PATH option

1. Download the latest GitHub CLI installer from [GitHub CLI Releases](https://github.com/cli/cli/releases/latest)
2. Run the installer (MSI file)
3. **Important:** During installation, make sure to check the option to "Add GitHub CLI to PATH"
4. Restart your computer after installation

### Option 2: Add GitHub CLI to PATH manually

If you've already installed GitHub CLI but didn't add it to PATH:

1. First, find where GitHub CLI is installed. It's typically in:
   ```
   C:\Program Files\GitHub CLI
   ```
   or
   ```
   C:\Users\[YourUsername]\AppData\Local\Programs\GitHub CLI
   ```

2. Add this location to your PATH:
   - Right-click on "This PC" or "My Computer" and select "Properties"
   - Click on "Advanced system settings"
   - Click the "Environment Variables" button
   - In the "System variables" section, find the "Path" variable and click "Edit"
   - Click "New" and add the path to the GitHub CLI installation directory
   - Click "OK" on all dialogs to save changes

3. Restart your command prompt and try again

### Option 3: Use the full path to the executable

If you don't want to modify your PATH, you can use the full path to the GitHub CLI executable:

```
"C:\Program Files\GitHub CLI\bin\gh.exe" --version
```

Replace the path with the actual location of gh.exe on your system.

## macOS Troubleshooting

If you installed via Homebrew:

```
brew doctor
brew link --overwrite gh
```

If that doesn't work, try:

```
echo 'export PATH="/usr/local/bin:$PATH"' >> ~/.bash_profile
source ~/.bash_profile
```

## Linux Troubleshooting

Depending on your distribution, try:

```
echo 'export PATH="/usr/local/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc
```

## Alternative: Use the Download Scripts Instead

If you're still having trouble with GitHub CLI, you can use the download scripts instead:

1. Run the download script:
   ```
   download-apk.bat
   ```

2. This will download a pre-built APK directly without needing GitHub CLI.

## Alternative: Manual GitHub Repository Creation

If GitHub CLI isn't working for you, you can manually create a repository and upload the files:

1. Go to [GitHub.com](https://github.com) and sign in
2. Click the "+" icon in the top-right corner and select "New repository"
3. Name your repository (e.g., "calendar-alarm")
4. Choose public or private
5. Click "Create repository"
6. Follow the instructions on the next page to upload your files:
   - You can use the "uploading an existing file" link to upload files directly through the browser
   - Or use Git commands if you have Git installed

## Need More Help?

If you're still having issues, please:

1. Check the [GitHub CLI documentation](https://cli.github.com/manual/)
2. Visit [GitHub CLI Discussions](https://github.com/cli/cli/discussions) for community help
3. Try the alternative installation methods in [ALTERNATIVE_BUILD_OPTIONS.md](ALTERNATIVE_BUILD_OPTIONS.md)
