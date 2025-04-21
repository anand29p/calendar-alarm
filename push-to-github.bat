@echo off
REM Script to create a GitHub repository and push the project to it

echo This script will help you create a GitHub repository and push this project to it.
echo You will need a GitHub account and the GitHub CLI (gh) installed.
echo.

REM Check if GitHub CLI is installed
where gh >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: GitHub CLI (gh) is not found or not in your PATH.
    echo.
    echo If you've already installed GitHub CLI, it might not be in your PATH.
    echo Please check the troubleshooting guide: GITHUB_CLI_TROUBLESHOOTING.md
    echo.
    echo If you haven't installed GitHub CLI yet, download it from:
    echo   - https://github.com/cli/cli/releases/latest
    echo.
    echo During installation, make sure to check the option to "Add GitHub CLI to PATH"
    echo After installation, restart your computer and try again.
    echo.
    echo Alternatively, you can try the download-apk.bat script instead.
    echo.
    pause
    exit /b 1
)

REM Check if user is logged in to GitHub
gh auth status >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo You are not logged in to GitHub. Please login first:
    gh auth login
    if %ERRORLEVEL% NEQ 0 (
        echo Failed to login to GitHub. Exiting.
        exit /b 1
    )
)

REM Ask for repository name
set REPO_NAME=calendar-alarm
set /p REPO_NAME=Enter a name for your GitHub repository (default: calendar-alarm): 

REM Ask for repository visibility
set PUBLIC=y
set /p PUBLIC=Make repository public? (y/n, default: y): 

if "%PUBLIC%"=="y" (
    set VISIBILITY=--public
) else (
    set VISIBILITY=--private
)

REM Create the repository
echo Creating GitHub repository: %REPO_NAME%
gh repo create "%REPO_NAME%" %VISIBILITY% --description "Android app that integrates with Google Calendar and provides alarm notifications" --source=. --push

if %ERRORLEVEL% NEQ 0 (
    echo Failed to create GitHub repository. Exiting.
    exit /b 1
)

echo.
echo Repository created and code pushed successfully!
echo.

REM Get the username
for /f "tokens=*" %%a in ('gh api user --jq ".login"') do set USERNAME=%%a
echo Your repository is available at: https://github.com/%USERNAME%/%REPO_NAME%
echo.
echo GitHub Actions will automatically build the APK. To download it:
echo 1. Go to your repository on GitHub
echo 2. Click on the 'Actions' tab
echo 3. Click on the latest workflow run
echo 4. Scroll down to the 'Artifacts' section
echo 5. Download the 'app-release-unsigned' artifact
echo.
echo Note: The first build might take a few minutes to complete.

echo.
echo Press any key to exit...
pause >nul
