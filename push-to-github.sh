#!/bin/bash

# Script to create a GitHub repository and push the project to it

echo "This script will help you create a GitHub repository and push this project to it."
echo "You will need a GitHub account and the GitHub CLI (gh) installed."
echo ""

# Check if GitHub CLI is installed
if ! command -v gh &> /dev/null; then
    echo "ERROR: GitHub CLI (gh) is not found or not in your PATH."
    echo ""
    echo "If you've already installed GitHub CLI, it might not be in your PATH."
    echo "Please check the troubleshooting guide: GITHUB_CLI_TROUBLESHOOTING.md"
    echo ""
    echo "If you haven't installed GitHub CLI yet, install it using one of these methods:"
    echo "  - Windows: https://github.com/cli/cli/releases/latest"
    echo "  - macOS: brew install gh"
    echo "  - Linux: https://github.com/cli/cli/blob/trunk/docs/install_linux.md"
    echo ""
    echo "After installation, you may need to restart your terminal or add it to your PATH."
    echo ""
    echo "Alternatively, you can try the download-apk.sh script instead."
    echo ""
    read -p "Press Enter to exit..."
    exit 1
fi

# Check if user is logged in to GitHub
if ! gh auth status &> /dev/null; then
    echo "You are not logged in to GitHub. Please login first:"
    gh auth login
    if [ $? -ne 0 ]; then
        echo "Failed to login to GitHub. Exiting."
        exit 1
    fi
fi

# Ask for repository name
read -p "Enter a name for your GitHub repository (default: calendar-alarm): " REPO_NAME
REPO_NAME=${REPO_NAME:-calendar-alarm}

# Ask for repository visibility
read -p "Make repository public? (y/n, default: y): " PUBLIC
PUBLIC=${PUBLIC:-y}

if [ "$PUBLIC" = "y" ]; then
    VISIBILITY="--public"
else
    VISIBILITY="--private"
fi

# Create the repository
echo "Creating GitHub repository: $REPO_NAME"
gh repo create "$REPO_NAME" $VISIBILITY --description "Android app that integrates with Google Calendar and provides alarm notifications" --source=. --push

if [ $? -ne 0 ]; then
    echo "Failed to create GitHub repository. Exiting."
    exit 1
fi

echo ""
echo "Repository created and code pushed successfully!"
echo ""
echo "Your repository is available at: https://github.com/$(gh api user | jq -r '.login')/$REPO_NAME"
echo ""
echo "GitHub Actions will automatically build the APK. To download it:"
echo "1. Go to your repository on GitHub"
echo "2. Click on the 'Actions' tab"
echo "3. Click on the latest workflow run"
echo "4. Scroll down to the 'Artifacts' section"
echo "5. Download the 'app-release-unsigned' artifact"
echo ""
echo "Note: The first build might take a few minutes to complete."
