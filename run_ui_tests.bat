@echo off
REM IONOS HiDrive Next - Android Client
REM SPDX-FileCopyrightText: 2025 STRATO AG.
REM SPDX-License-Identifier: GPL-2.0
REM
REM UI Test Runner Script for Windows
REM Run from the project root directory (HDNextUiTests)

setlocal enabledelayedexpansion

set PACKAGE_NAME=com.ionos.hidrivenext
set TEST_PACKAGE=com.ionos.hidrivenext.test.tests

REM Parse arguments
set TEST_CLASS=
set TEST_METHOD=
set FULL_ARG=%~1

if "%FULL_ARG%"=="" goto run_all
if "%FULL_ARG%"=="--help" goto show_help
if "%FULL_ARG%"=="-h" goto show_help

REM Check if argument contains #
echo %FULL_ARG% | find "#" > nul
if %errorlevel%==0 (
    for /f "tokens=1,2 delims=#" %%a in ("%FULL_ARG%") do (
        set TEST_CLASS=%%a
        set TEST_METHOD=%%b
    )
) else (
    set TEST_CLASS=%FULL_ARG%
)

goto main

:show_help
echo Usage: %0 [TEST_CLASS[#TEST_METHOD]]
echo.
echo Examples:
echo   %0                                          Run all tests
echo   %0 LoginLogoutTest                          Run specific test class
echo   %0 LoginLogoutTest#test_complete_login_logout_flow   Run specific test
echo.
goto end

:main
echo =========================================
echo HiDrive Next UI Test Runner (Windows)
echo =========================================
echo.

REM Check for emulator
echo Checking for emulator...
adb devices | find "device" > nul
if %errorlevel% neq 0 (
    echo ERROR: No Android emulator/device detected
    echo Please start an emulator or connect a device first
    goto end
)
echo [OK] Emulator detected
echo.

REM Enable network
echo Enabling network...
adb shell svc wifi enable 2>nul
adb shell svc data enable 2>nul
adb shell settings put global airplane_mode_on 0 2>nul
timeout /t 2 /nobreak > nul
echo [OK] Network enabled
echo.

REM Clear app data
echo Clearing app data...
adb shell pm clear %PACKAGE_NAME% 2>nul
echo [OK] App data cleared
echo.

REM Build and install
echo Building and installing app...
call gradlew.bat installGplayDebug installGplayDebugAndroidTest
if %errorlevel% neq 0 (
    echo ERROR: Build failed
    goto end
)
echo [OK] App installed
echo.

REM Grant permissions
echo Granting permissions...
adb shell pm grant %PACKAGE_NAME% android.permission.POST_NOTIFICATIONS 2>nul
adb shell appops set %PACKAGE_NAME% MANAGE_EXTERNAL_STORAGE allow 2>nul
echo [OK] Permissions granted
echo.

REM Run tests
echo =========================================
echo Running Tests
echo =========================================
echo.

REM Determine test class package
set UITESTS_CLASSES=LoginLogoutSmokeTest LoginLogoutTest NavigationTest
set IS_UITEST=0

for %%c in (%UITESTS_CLASSES%) do (
    if "%TEST_CLASS%"=="%%c" set IS_UITEST=1
)

if defined TEST_CLASS (
    if defined TEST_METHOD (
        if %IS_UITEST%==1 (
            set ADB_CLASS=com.ionos.hidrivenext.uitests.%TEST_CLASS%#%TEST_METHOD%
        ) else (
            set ADB_CLASS=%TEST_PACKAGE%.%TEST_CLASS%#%TEST_METHOD%
        )
        echo Running: %TEST_CLASS%#%TEST_METHOD%
    ) else (
        if %IS_UITEST%==1 (
            set ADB_CLASS=com.ionos.hidrivenext.uitests.%TEST_CLASS%
        ) else (
            set ADB_CLASS=%TEST_PACKAGE%.%TEST_CLASS%
        )
        echo Running: %TEST_CLASS%
    )
    echo.
    adb shell am instrument -w -e class !ADB_CLASS! %PACKAGE_NAME%.test/com.nextcloud.client.TestRunner
) else (
    :run_all
    echo Running all tests in %TEST_PACKAGE%
    echo.
    adb shell am instrument -w -e package %TEST_PACKAGE% %PACKAGE_NAME%.test/com.nextcloud.client.TestRunner
)

if %errorlevel% neq 0 (
    echo.
    echo =========================================
    echo Tests Failed!
    echo =========================================
) else (
    echo.
    echo =========================================
    echo Tests Passed!
    echo =========================================
)

:end
endlocal
