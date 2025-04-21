# GitHub CLI Guide for Calendar Alarm App

This guide will help you use GitHub CLI to create a repository and build the Calendar Alarm app APK.

## Basic GitHub CLI Commands

GitHub CLI (`gh`) is a command-line tool that brings GitHub functionality to your terminal. Here's how to use it:

## Step 1: Verify GitHub CLI Installation

First, let's verify that GitHub CLI is installed correctly:

1. Open a terminal or command prompt
2. Type the following command and press Enter:
   ```
   gh --version
   ```
3. You should see output like `gh version 2.x.x`

If you get a "command not found" error or `'gh' is not recognized as an internal or external command`, GitHub CLI might not be in your PATH. 

**Having trouble?** See our detailed [GitHub CLI Troubleshooting Guide](GITHUB_CLI_TROUBLESHOOTING.md) for step-by-step instructions to fix PATH issues and other common problems.

## Step 2: Log in to GitHub

Before using GitHub CLI, you need to log in to your GitHub account:

1. Open a terminal or command prompt
2. Type the following command and press Enter:
   ```
   gh auth login
   ```
3. Follow the interactive prompts:
   - Select "GitHub.com" (not GitHub Enterprise)
   - Choose your preferred protocol (HTTPS is recommended)
   - When asked to authenticate, select "Login with a web browser"
   - Copy the one-time code shown in the terminal
   - Press Enter to open the browser
   - Paste the code in the browser when prompted
   - Authorize GitHub CLI to access your GitHub account

## Step 3: Create a Repository and Push the Calendar Alarm App

Instead of running our script manually, you can use these commands directly:

1. Navigate to the Calendar Alarm app directory:
   ```
   cd path/to/calendar-alarm
   ```

2. Create a new repository on GitHub:
   ```
   gh repo create calendar-alarm --public --source=. --push
   ```
   - This creates a public repository named "calendar-alarm"
   - `--source=.` tells GitHub CLI to use the current directory
   - `--push` automatically pushes your code to the new repository

3. Verify that your repository was created:
   ```
   gh repo view --web
   ```
   - This opens your new repository in a web browser

## Step 4: Wait for GitHub Actions to Build the APK

After pushing your code to GitHub, GitHub Actions will automatically start building the APK:

1. Go to the Actions tab in your repository (in the browser)
2. You should see a workflow named "Android CI" running
3. Wait for the workflow to complete (this may take a few minutes)
4. Once completed, click on the workflow run
5. Scroll down to the "Artifacts" section
6. Click on "app-release-unsigned" to download the APK

## Step 5: Install the APK on Your Android Device

1. Transfer the downloaded APK to your Android device
2. On your Android device, go to Settings > Security
3. Enable "Unknown sources" or "Install unknown apps" (varies by Android version)
4. Open the APK file on your device to install it

## Troubleshooting GitHub CLI

If you encounter issues with GitHub CLI:

### Command Not Found

If you get a "command not found" error when trying to run `gh`:

- **Windows**: Make sure GitHub CLI is in your PATH. Try reinstalling and selecting the option to add to PATH.
- **macOS**: If you installed via Homebrew, try `brew link gh`
- **Linux**: Make sure the installation directory is in your PATH

### Authentication Issues

If you have trouble logging in:

1. Try the alternative authentication method:
   ```
   gh auth login --web
   ```

2. If that doesn't work, try generating a personal access token:
   - Go to GitHub.com > Settings > Developer settings > Personal access tokens
   - Generate a new token with "repo" and "workflow" scopes
   - Use this token when prompted by `gh auth login`

### Repository Creation Issues

If you have trouble creating a repository:

1. Make sure you're in the correct directory:
   ```
   cd path/to/calendar-alarm
   ```

2. Try creating the repository on GitHub.com first, then push your code:
   ```
   gh repo create calendar-alarm --public
   git remote add origin https://github.com/YOUR_USERNAME/calendar-alarm.git
   git branch -M main
   git push -u origin main
   ```

## Additional GitHub CLI Commands

Here are some other useful GitHub CLI commands:

- List your repositories:
  ```
  gh repo list
  ```

- View repository details:
  ```
  gh repo view
  ```

- Create an issue:
  ```
  gh issue create
  ```

- Check workflow status:
  ```
  gh workflow list
  ```

For more commands and help, run:
```
gh help
