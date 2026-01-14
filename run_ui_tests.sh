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
# To add new tests:
# 1. Create test class in the appropriate package
# 2. Tests in 'uitests' package are auto-detected
# 3. Tests in 'test.tests' package are auto-detected
# No script changes needed!
#

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"
PACKAGE_NAME="com.ionos.hidrivenext"

# Test packages - add new packages here as needed
declare -A TEST_PACKAGES=(
    ["uitests"]="com.ionos.hidrivenext.uitests"
    ["tests"]="com.ionos.hidrivenext.test.tests"
)

# Default package for running all tests
DEFAULT_TEST_PACKAGE="com.ionos.hidrivenext.uitests"

# Parse arguments
TEST_CLASS=""
TEST_METHOD=""
CLEAN_BUILD=false
SETUP_ONLY=false
LIST_TESTS=false
SKIP_BUILD=false

show_help() {
    echo "Usage: $0 [OPTIONS] [TEST_CLASS[#TEST_METHOD]]"
    echo ""
    echo "Options:"
    echo "  --clean         Clean and rebuild before running tests"
    echo "  --setup         Only setup test environment (install app, grant permissions)"
    echo "  --list          List available test classes"
    echo "  --skip-build    Skip build step (use if already built)"
    echo "  --help, -h      Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                                              # Run all tests"
    echo "  $0 LoginLogoutTest                              # Run test class"
    echo "  $0 LoginLogoutTest#test_complete_login_logout_flow  # Run specific test"
    echo "  $0 --list                                       # List available tests"
    echo "  $0 --skip-build LoginLogoutTest                 # Run without rebuilding"
    echo ""
    echo "Available test classes:"
    list_tests
}

list_tests() {
    echo ""
    echo "uitests package (com.ionos.hidrivenext.uitests):"
    find "$PROJECT_DIR/HdNext/ui-tests/src/main/kotlin/com/ionos/hidrivenext/uitests" \
        -name "*.kt" -type f 2>/dev/null | while read -r file; do
        basename "$file" .kt | sed 's/^/  - /'
    done
    
    echo ""
    echo "test.tests package (com.ionos.hidrivenext.test.tests):"
    find "$PROJECT_DIR/HdNext/ui-tests/src/main/kotlin/com/ionos/hidrivenext/test/tests" \
        -name "*.kt" -type f 2>/dev/null | while read -r file; do
        basename "$file" .kt | sed 's/^/  - /'
    done
    echo ""
}

# Determine which package a test class belongs to
get_test_package() {
    local test_class="$1"
    
    # Check uitests package
    if [ -f "$PROJECT_DIR/HdNext/ui-tests/src/main/kotlin/com/ionos/hidrivenext/uitests/${test_class}.kt" ]; then
        echo "${TEST_PACKAGES[uitests]}"
        return
    fi
    
    # Check test.tests package
    if [ -f "$PROJECT_DIR/HdNext/ui-tests/src/main/kotlin/com/ionos/hidrivenext/test/tests/${test_class}.kt" ]; then
        echo "${TEST_PACKAGES[tests]}"
        return
    fi
    
    # Default: assume it's in test.tests (for backward compatibility)
    echo "$DEFAULT_TEST_PACKAGE"
}

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
        --list)
            LIST_TESTS=true
            shift
            ;;
        --skip-build)
            SKIP_BUILD=true
            shift
            ;;
        --help|-h)
            show_help
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

# Handle --list
if [ "$LIST_TESTS" = true ]; then
    list_tests
    exit 0
fi

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

# Function to grant required permissions
grant_permissions() {
    echo -e "${YELLOW}Granting permissions...${NC}"
    adb shell pm grant $PACKAGE_NAME android.permission.POST_NOTIFICATIONS 2>/dev/null || true
    adb shell appops set $PACKAGE_NAME MANAGE_EXTERNAL_STORAGE allow 2>/dev/null || true
    echo -e "${GREEN}✓ Permissions granted${NC}"
}

# Function to build and install app
build_and_install() {
    if [ "$SKIP_BUILD" = true ]; then
        echo -e "${YELLOW}Skipping build (--skip-build)${NC}"
        return
    fi
    
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

    local adb_class=""
    local test_output=""
    
    if [ -n "$TEST_CLASS" ]; then
        # Auto-detect package for the test class
        local test_package
        test_package=$(get_test_package "$TEST_CLASS")
        
        if [ -n "$TEST_METHOD" ]; then
            adb_class="${test_package}.${TEST_CLASS}#${TEST_METHOD}"
            echo -e "${YELLOW}Running: ${TEST_CLASS}#${TEST_METHOD}${NC}"
            echo -e "${YELLOW}Package: ${test_package}${NC}"
        else
            adb_class="${test_package}.${TEST_CLASS}"
            echo -e "${YELLOW}Running: ${TEST_CLASS}${NC}"
            echo -e "${YELLOW}Package: ${test_package}${NC}"
        fi
        
        echo ""
        test_output=$(adb shell am instrument -w -e class "$adb_class" \
            com.ionos.hidrivenext.test/com.nextcloud.client.TestRunner 2>&1)
    else
        echo -e "${YELLOW}Running all tests in ${DEFAULT_TEST_PACKAGE}${NC}"
        echo ""
        test_output=$(adb shell am instrument -w -e package "$DEFAULT_TEST_PACKAGE" \
            com.ionos.hidrivenext.test/com.nextcloud.client.TestRunner 2>&1)
    fi

    # Print the output
    echo "$test_output"
    echo ""
    
    # Check for test failures in the output
    if echo "$test_output" | grep -q "FAILURES\!\!\!" || \
       echo "$test_output" | grep -E "Failures: [1-9]" > /dev/null 2>&1 || \
       echo "$test_output" | grep -q "Process crashed" || \
       echo "$test_output" | grep -q "Empty test suite"; then
        echo ""
        echo -e "${RED}=========================================${NC}"
        echo -e "${RED}Tests Failed!${NC}"
        echo -e "${RED}=========================================${NC}"
        exit 1
    fi

    # Also check if tests actually ran (look for "OK" or test count)
    if ! echo "$test_output" | grep -qE "(OK \([0-9]+ test|Tests run: [0-9]+)"; then
        echo ""
        echo -e "${RED}=========================================${NC}"
        echo -e "${RED}Tests Failed to Execute!${NC}"
        echo -e "${RED}=========================================${NC}"
        exit 1
    fi
    
    echo ""
    echo -e "${GREEN}=========================================${NC}"
    echo -e "${GREEN}Tests Passed!${NC}"
    echo -e "${GREEN}=========================================${NC}"
}

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
    echo "List tests with: $0 --list"
    exit 0
fi

# Step 6: Run tests
run_tests

echo ""
echo -e "${GREEN}Done!${NC}"
