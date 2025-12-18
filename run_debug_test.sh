#!/bin/bash
#
# IONOS HiDrive Next - Android Client
#
# SPDX-FileCopyrightText: 2025 STRATO AG.
# SPDX-License-Identifier: GPL-2.0
#
# Build and install app for manual testing
# Run from the project root directory (HDNextUiTests)
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "========================================="
echo "Building and Installing HiDrive Next"
echo "========================================="
echo ""
echo "After installation, you can:"
echo "1. Manually navigate through the login flow"
echo "2. Open chrome://inspect to view WebView"
echo "3. Use Layout Inspector to check element IDs"
echo "4. Note down timings and element selectors"
echo ""

echo "Step 0: Enabling internet on emulator..."
adb shell svc wifi enable
adb shell svc data enable
adb shell settings put global airplane_mode_on 0
echo "✓ Network enabled"
echo ""

echo "Step 1: Building app..."
./gradlew clean :app:assembleGplayDebug
echo ""

echo "Step 2: Finding APK..."
APK=$(find app/build/outputs/apk/gplay/debug -name "*.apk" | head -1)
if [ -z "$APK" ]; then
    echo "ERROR: No APK found after build!"
    exit 1
fi
echo "Found: $APK"
echo ""

echo "Step 3: Uninstalling old versions..."
adb uninstall com.ionos.hidrivenext 2>/dev/null || echo "No previous version"
adb uninstall com.nextcloud.client 2>/dev/null || echo "No Nextcloud version"
echo ""

echo "Step 4: Installing HiDrive Next..."
adb install "$APK"
echo ""

echo "Step 5: Granting permissions..."
adb shell pm grant com.ionos.hidrivenext android.permission.POST_NOTIFICATIONS 2>/dev/null || true
adb shell appops set com.ionos.hidrivenext MANAGE_EXTERNAL_STORAGE allow 2>/dev/null || true
echo "✓ Permissions granted"
echo ""

echo "Step 6: Launching app..."
adb shell monkey -p com.ionos.hidrivenext -c android.intent.category.LAUNCHER 1 > /dev/null 2>&1
sleep 2
echo ""

echo "========================================="
echo "App Installed and Launched!"
echo "========================================="
echo ""
echo "Now you can:"
echo "1. Manually test the login flow"
echo "2. Open Chrome → chrome://inspect"
echo "3. Inspect the OIDC WebView when it appears"
echo "4. Note down element IDs and CSS selectors"
echo ""
echo "Things to check:"
echo "- How long does WebView take to appear?"
echo "- What is the actual WebView ID?"
echo "- What are the actual login form element IDs?"
echo "- Are there any iframes?"
echo ""
