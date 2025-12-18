#!/bin/bash
#
# IONOS HiDrive Next - Android Client
#
# SPDX-FileCopyrightText: 2025 STRATO AG.
# SPDX-License-Identifier: GPL-2.0
#
# UI Test Runner Script
# Run from the project root directory (HDNextUiTests)
#

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration - use script directory as project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"
PACKAGE_NAME="com.ionos.hidrivenext"
TEST_PACKAGE="com.ionos.hidrivenext.test.tests"

# Parse arguments
TEST_CLASS=""
TEST_METHOD=""
CLEAN_BUILD=false
SETUP_ONLY=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --clean)
            CLEAN_BUILD=true
            shift
            ;;
        --setup)
            SETUP_ONLY=true
            shift
            ;;
        --help|-h)
            echo "Usage: $0 [OPTIONS] [TEST_CLASS[#TEST_METHOD]]"
            echo ""
            echo "Options:"
            echo "  --clean         Clean and rebuild before running tests"
            echo "  --setup         Only setup test environment (install app, enable network)"
            echo "  --help, -h      Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                                      # Run all tests"
            echo "  $0 OIDCLoginTest                        # Run specific test class"
            echo "  $0 OIDCLoginTest#user_can_complete_oidc_login  # Run specific test method"
            echo "  $0 --clean OIDCLoginTest                # Clean build then run tests"
            echo "  $0 --setup                              # Setup environment only"
            echo "  $0 LoginLogoutTest                      # Run login/logout test"
            echo "  $0 LoginLogoutTest#test_complete_login_logout_flow  # Run specific test"
            exit 0
            ;;
        *)
            if [[ "$1" == *"#"* ]]; then
                TEST_CLASS="${1%%#*}"
                TEST_METHOD="${1##*#}"
            else
                TEST_CLASS="$1"
            fi
            shift
            ;;
    esac
done

cd "$PROJECT_DIR"

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}HiDrive Next UI Test Runner${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Function to check if emulator is running
check_emulator() {
    if ! adb devices | grep -q "device$"; then
        echo -e "${RED}ERROR: No Android emulator/device detected${NC}"
        echo "Please start an emulator or connect a device first"
        exit 1
    fi
    echo -e "${GREEN}✓ Emulator/device detected${NC}"
}

# Function to enable network on emulator
enable_network() {
    echo -e "${YELLOW}Enabling network on emulator...${NC}"
    adb shell svc wifi enable 2>/dev/null || true
    adb shell svc data enable 2>/dev/null || true
    adb shell settings put global airplane_mode_on 0 2>/dev/null || true
    sleep 2
    echo -e "${GREEN}✓ Network enabled${NC}"
}

# Function to clear app data for fresh test state
clear_app_data() {
    echo -e "${YELLOW}Clearing app data for fresh state...${NC}"
    adb shell pm clear $PACKAGE_NAME 2>/dev/null || echo "App not installed yet"
    echo -e "${GREEN}✓ App data cleared${NC}"
}

# Function to grant required permissions (must be called AFTER app is installed)
grant_permissions() {
    echo -e "${YELLOW}Granting permissions...${NC}"
    adb shell pm grant $PACKAGE_NAME android.permission.POST_NOTIFICATIONS 2>/dev/null || true
    adb shell appops set $PACKAGE_NAME MANAGE_EXTERNAL_STORAGE allow 2>/dev/null || true
    echo -e "${GREEN}✓ Permissions granted${NC}"
}

# Function to build and install app
build_and_install() {
    echo -e "${YELLOW}Building and installing app...${NC}"
    if [ "$CLEAN_BUILD" = true ]; then
        ./gradlew clean installGplayDebug installGplayDebugAndroidTest
    else
        ./gradlew installGplayDebug installGplayDebugAndroidTest
    fi
    echo -e "${GREEN}✓ App installed${NC}"
}

# Function to run tests
run_tests() {
    echo ""
    echo -e "${BLUE}=========================================${NC}"
    echo -e "${BLUE}Running Tests${NC}"
    echo -e "${BLUE}=========================================${NC}"
    echo ""

    # Determine test class package
    # uitests package: LoginLogoutSmokeTest, LoginLogoutTest, NavigationTest
    # test.tests package: OIDCLoginTest, etc.
    local uitests_classes="LoginLogoutSmokeTest|LoginLogoutTest|NavigationTest"
    
    if [ -n "$TEST_CLASS" ] && [ -n "$TEST_METHOD" ]; then
        if [[ "$TEST_CLASS" =~ ^($uitests_classes)$ ]]; then
            ADB_CLASS="com.ionos.hidrivenext.uitests.${TEST_CLASS}#${TEST_METHOD}"
        else
            ADB_CLASS="${TEST_PACKAGE}.${TEST_CLASS}#${TEST_METHOD}"
        fi
        echo -e "${YELLOW}Running: ${TEST_CLASS}#${TEST_METHOD}${NC}"
    elif [ -n "$TEST_CLASS" ]; then
        if [[ "$TEST_CLASS" =~ ^($uitests_classes)$ ]]; then
            ADB_CLASS="com.ionos.hidrivenext.uitests.${TEST_CLASS}"
        else
            ADB_CLASS="${TEST_PACKAGE}.${TEST_CLASS}"
        fi
        echo -e "${YELLOW}Running: ${TEST_CLASS}${NC}"
    else
        ADB_CLASS=""
        echo -e "${YELLOW}Running all tests in ${TEST_PACKAGE}${NC}"
    fi

    echo ""

    # Run the tests using adb instrument
    if [ -n "$ADB_CLASS" ]; then
        adb shell am instrument -w -e class "$ADB_CLASS" \
            com.ionos.hidrivenext.test/com.nextcloud.client.TestRunner
    else
        adb shell am instrument -w -e package "$TEST_PACKAGE" \
            com.ionos.hidrivenext.test/com.nextcloud.client.TestRunner
    fi

    TEST_RESULT=$?

    if [ $TEST_RESULT -ne 0 ]; then
        echo ""
        echo -e "${RED}=========================================${NC}"
        echo -e "${RED}Tests Failed!${NC}"
        echo -e "${RED}=========================================${NC}"
        exit 1
    fi

    echo ""
    echo -e "${GREEN}=========================================${NC}"
    echo -e "${GREEN}Tests Passed!${NC}"
    echo -e "${GREEN}=========================================${NC}"
}

# Main execution flow
echo "Configuration:"
echo "  Project: $PROJECT_DIR"
echo "  Package: $PACKAGE_NAME"
if [ -n "$TEST_CLASS" ]; then
    echo "  Test: $TEST_CLASS${TEST_METHOD:+#$TEST_METHOD}"
fi
echo ""

# Step 1: Check emulator
check_emulator

# Step 2: Enable network
enable_network

# Step 3: Clear app data for fresh state
clear_app_data

# Step 4: Build and install app
build_and_install

# Step 5: Grant required permissions
grant_permissions

# If setup only, exit here
if [ "$SETUP_ONLY" = true ]; then
    echo ""
    echo -e "${GREEN}=========================================${NC}"
    echo -e "${GREEN}Setup Complete!${NC}"
    echo -e "${GREEN}=========================================${NC}"
    echo ""
    echo "Run tests with: $0 [TEST_CLASS]"
    exit 0
fi

# Step 6: Run tests
run_tests

echo ""
echo -e "${GREEN}Done!${NC}"
