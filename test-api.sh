#!/bin/bash

BASE_URL="http://localhost:8080/AuthorizationManagementSystem/api/v1"

USERNAME="rajeb"
EMAIL="rajeb@gmail.com"
PASSWORD="Password@123"
FULL_NAME="Md Abdullah Rajeb"

PASS=0
FAIL=0

echo "=============================================="
echo " Authorization Management System API Test"
echo "=============================================="
echo

run_test() {
    NAME="$1"
    EXPECTED="$2"
    shift 2

    echo "----------------------------------------------"
    echo "TEST: $NAME"

    RESPONSE=$(curl -s -w "\n%{http_code}" "$@")
    STATUS=$(echo "$RESPONSE" | tail -n1)
    BODY=$(echo "$RESPONSE" | sed '$d')

    echo "HTTP: $STATUS"
    echo "Response: $BODY"

    if [ "$STATUS" = "$EXPECTED" ]; then
        echo "RESULT: PASS"
        PASS=$((PASS + 1))
    else
        echo "RESULT: FAIL (expected $EXPECTED)"
        FAIL=$((FAIL + 1))
    fi

    echo
}

# ------------------------------------------------
# 1. Protected endpoint without token
# ------------------------------------------------

run_test \
    "GET /users without JWT" \
    "401" \
    "$BASE_URL/users"

# ------------------------------------------------
# 2. Register user
# ------------------------------------------------

echo "----------------------------------------------"
echo "TEST: POST /users - Registration"

REGISTER_RESPONSE=$(curl -s -w "\n%{http_code}" \
    -X POST "$BASE_URL/users" \
    -H "Content-Type: application/json" \
    -d "{
        \"username\": \"$USERNAME\",
        \"email\": \"$EMAIL\",
        \"password\": \"$PASSWORD\",
        \"fullName\": \"$FULL_NAME\"
    }")

REGISTER_STATUS=$(echo "$REGISTER_RESPONSE" | tail -n1)
REGISTER_BODY=$(echo "$REGISTER_RESPONSE" | sed '$d')

echo "HTTP: $REGISTER_STATUS"
echo "Response: $REGISTER_BODY"

if [ "$REGISTER_STATUS" = "201" ]; then
    echo "RESULT: PASS - User created"
    PASS=$((PASS + 1))
elif [ "$REGISTER_STATUS" = "400" ]; then
    echo "RESULT: INFO - User already exists, continuing"
else
    echo "RESULT: FAIL"
    FAIL=$((FAIL + 1))
fi

echo

# ------------------------------------------------
# 3. Login
# ------------------------------------------------

echo "----------------------------------------------"
echo "TEST: POST /auth/login"

LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" \
    -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{
        \"username\": \"$USERNAME\",
        \"password\": \"$PASSWORD\"
    }")

LOGIN_STATUS=$(echo "$LOGIN_RESPONSE" | tail -n1)
LOGIN_BODY=$(echo "$LOGIN_RESPONSE" | sed '$d')

echo "HTTP: $LOGIN_STATUS"
echo "Response: $LOGIN_BODY"

if [ "$LOGIN_STATUS" = "200" ]; then
    echo "RESULT: PASS"
    PASS=$((PASS + 1))
else
    echo "RESULT: FAIL"
    FAIL=$((FAIL + 1))
fi

echo

# Extract JWT
TOKEN=$(echo "$LOGIN_BODY" | sed -n 's/.*"token"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p')

if [ -z "$TOKEN" ]; then
    echo "=============================================="
    echo "ERROR: JWT token could not be extracted."
    echo "Login response was:"
    echo "$LOGIN_BODY"
    echo "=============================================="
    exit 1
fi

echo "JWT token successfully extracted."
echo

# ------------------------------------------------
# 4. Protected /users with JWT
# ------------------------------------------------

run_test \
    "GET /users with JWT" \
    "200" \
    -H "Authorization: Bearer $TOKEN" \
    "$BASE_URL/users"

# ------------------------------------------------
# 5. Invalid JWT
# ------------------------------------------------

run_test \
    "GET /users with invalid JWT" \
    "401" \
    -H "Authorization: Bearer invalid-token" \
    "$BASE_URL/users"

# ------------------------------------------------
# 6. Empty Bearer token
# ------------------------------------------------

run_test \
    "GET /users with empty Bearer token" \
    "401" \
    -H "Authorization: Bearer " \
    "$BASE_URL/users"

# ------------------------------------------------
# 7. Wrong password
# ------------------------------------------------

run_test \
    "Login with wrong password" \
    "401" \
    -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{
        \"username\": \"$USERNAME\",
        \"password\": \"WrongPassword@123\"
    }"

# ------------------------------------------------
# 8. Non-existent user
# ------------------------------------------------

run_test \
    "Login with non-existent user" \
    "401" \
    -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "user_that_does_not_exist",
        "password": "Password@123"
    }'

# ------------------------------------------------
# 9. Empty login JSON
# ------------------------------------------------

run_test \
    "Login with empty JSON" \
    "400" \
    -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{}'

# ------------------------------------------------
# 10. Invalid JSON
# ------------------------------------------------

run_test \
    "Login with invalid JSON" \
    "400" \
    -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{invalid-json'

# ------------------------------------------------
# 11. Roles
# ------------------------------------------------

run_test \
    "GET /roles with JWT" \
    "200" \
    -H "Authorization: Bearer $TOKEN" \
    "$BASE_URL/roles"

# ------------------------------------------------
# 12. Permissions
# ------------------------------------------------

run_test \
    "GET /permissions with JWT" \
    "200" \
    -H "Authorization: Bearer $TOKEN" \
    "$BASE_URL/permissions"

# ------------------------------------------------
# 13. Access Requests
# ------------------------------------------------

run_test \
    "GET /access-requests with JWT" \
    "200" \
    -H "Authorization: Bearer $TOKEN" \
    "$BASE_URL/access-requests"

# ------------------------------------------------
# 14. Approvals
# ------------------------------------------------

run_test \
    "GET /approvals with JWT" \
    "200" \
    -H "Authorization: Bearer $TOKEN" \
    "$BASE_URL/approvals"

# ------------------------------------------------
# 15. Audits
# ------------------------------------------------

run_test \
    "GET /audits with JWT" \
    "200" \
    -H "Authorization: Bearer $TOKEN" \
    "$BASE_URL/audits"

# ------------------------------------------------
# 16. Reports
# ------------------------------------------------

run_test \
    "GET /reports/summary with JWT" \
    "200" \
    -H "Authorization: Bearer $TOKEN" \
    "$BASE_URL/reports/summary"

# ------------------------------------------------
# Summary
# ------------------------------------------------

echo "=============================================="
echo " TEST SUMMARY"
echo "=============================================="
echo "PASS: $PASS"
echo "FAIL: $FAIL"
echo "=============================================="

if [ "$FAIL" -eq 0 ]; then
    echo "ALL TESTS PASSED"
    exit 0
else
    echo "SOME TESTS FAILED"
    exit 1
fi
