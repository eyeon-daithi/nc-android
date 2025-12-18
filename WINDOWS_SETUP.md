# HiDrive Next UI Tests - Windows Setup Guide

## Prerequisites

1. **Android Studio** with Android SDK installed
2. **Java 17** (comes with Android Studio or install separately)
3. **Git for Windows** with symlink support enabled
4. **Android Emulator** running (Pixel 8 API 35 recommended)

## One-Time Setup

### 1. Enable Git Symlinks (Required)

Run Git Bash **as Administrator** and configure:

```bash
git config --global core.symlinks true
```

Then re-clone the repository (existing clones won't have working symlinks):

```bash
git clone --recurse-submodules git@github.com:IONOS-Productivity/nc-android.git
```

### 2. Alternative: Manual Symlink Setup (if git symlinks don't work)

If symlinks don't work, create a junction instead. Open **Command Prompt as Administrator**:

```cmd
cd path\to\HDNextUiTests\app\src\androidTest\kotlin\com\ionos\hidrivenext
mklink /J test ..\..\..\..\..\..\..\HdNext\ui-tests\src\main\kotlin\com\ionos\hidrivenext\test
mklink /J uitests ..\..\..\..\..\..\..\HdNext\ui-tests\src\main\kotlin\com\ionos\hidrivenext\uitests
```

### 3. Configure Test Credentials

Create/edit `C:\Users\<YourUsername>\.gradle\gradle.properties`:

```properties
# HiDrive Next UI Test Credentials
NC_TEST_SERVER_BASEURL=https://storage.ionos.fr
NC_TEST_SERVER_USERNAME=your-test-email@example.com
NC_TEST_SERVER_PASSWORD=your-test-password
```

## Running Tests

### Option A: Using Android Studio (Recommended for Windows)

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Start an emulator
4. **First, grant permissions manually:**
   ```
   adb shell pm grant com.ionos.hidrivenext android.permission.POST_NOTIFICATIONS
   ```
5. Navigate to: `app/src/androidTest/kotlin/com/ionos/hidrivenext/uitests/LoginLogoutTest.kt`
6. Right-click on `test_complete_login_logout_flow` → **Run**

> **If you can't see the test files:** The symlinks may not be working. 
> Follow "Alternative: Manual Symlink Setup" above, then restart Android Studio.

### Available Test Classes

| Class | Package | Description |
|-------|---------|-------------|
| LoginLogoutTest | uitests | Full login/logout E2E test |
| LoginLogoutSmokeTest | uitests | Quick smoke test |
| NavigationTest | uitests | Navigation tests |
| OIDCLoginTest | test.tests | OIDC-specific tests |

### Option B: Using Command Line (Git Bash or PowerShell)

Open Git Bash or PowerShell in the project directory:

```bash
# Build and install
./gradlew installGplayDebug installGplayDebugAndroidTest

# Grant permissions
adb shell pm grant com.ionos.hidrivenext android.permission.POST_NOTIFICATIONS

# Run the test
adb shell am instrument -w -e class com.ionos.hidrivenext.uitests.LoginLogoutTest#test_complete_login_logout_flow com.ionos.hidrivenext.test/com.nextcloud.client.TestRunner
```

### Option C: Using the Batch File

Run `run_ui_tests.bat` from Command Prompt:

```cmd
run_ui_tests.bat LoginLogoutTest#test_complete_login_logout_flow
```

## Troubleshooting

### "Class not found" errors
- Ensure symlinks/junctions are set up correctly
- Run `./gradlew clean` and rebuild

### Notification permission dialog blocks test
- Manually tap "Allow" on the emulator, or
- Run: `adb shell pm grant com.ionos.hidrivenext android.permission.POST_NOTIFICATIONS`

### WebView doesn't load
- Check emulator has internet access
- Run: `adb shell ping -c 3 google.com`

### Test credentials not working
- Verify `~/.gradle/gradle.properties` has correct values
- Run `./gradlew clean installGplayDebug` to rebuild with new credentials

## Test Structure

```
HdNext/ui-tests/src/main/kotlin/com/ionos/hidrivenext/
├── test/
│   ├── config/         # BaseTest, TestAccount
│   ├── pages/          # Page Objects (WelcomePage, FilesPage, etc.)
│   ├── robots/         # AuthRobot
│   └── utils/          # Waits, WebViewHelpers
└── uitests/
    └── LoginLogoutTest.kt  # Main E2E test
```
