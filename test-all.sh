
#!/usr/bin/env bash

BASE_URL="http://localhost:8080/AuthorizationManagementSystem/api/v1"

ADMIN_USER="rajeb"
ADMIN_PASS="Password@123"

PASS_COUNT=0
FAIL_COUNT=0
INFO_COUNT=0

TEST_USER="api_test_$(date +%s)"
TEST_EMAIL="${TEST_USER}@example.com"
TEST_PASS="Test@12345"
TEST_FULL_NAME="API Test User"

TOKEN=""
TEST_USER_ID=""
TEST_ROLE_ID=""

print_header() {
    echo
    echo "=============================================="
    echo "$1"
    echo "=============================================="
}

print_test() {
    echo
    echo "----------------------------------------------"
    echo "TEST: $1"
}

pass() {
    echo "RESULT: PASS"
    PASS_COUNT=$((PASS_COUNT + 1))
}

fail() {
    echo "RESULT: FAIL"
    FAIL_COUNT=$((FAIL_COUNT + 1))
}

info() {
    echo "RESULT: INFO - $1"
    INFO_COUNT=$((INFO_COUNT + 1))
}

request() {
    RESPONSE_FILE=$(mktemp)

    HTTP_CODE=$(curl -sS \
        -o "$RESPONSE_FILE" \
        -w "%{http_code}" \
        "$@" 2>/dev/null)

    RESPONSE=$(cat "$RESPONSE_FILE")
    rm -f "$RESPONSE_FILE"
}

expect_status() {
    EXPECTED="$1"

    echo "HTTP: $HTTP_CODE"
    echo "Response: $RESPONSE"

    if [ "$HTTP_CODE" = "$EXPECTED" ]; then
        pass
    else
        echo "Expected HTTP: $EXPECTED"
        fail
    fi
}

json_string() {
    echo "$RESPONSE" |
        sed -n "s/.*\"$1\":\"\([^\"]*\)\".*/\1/p" |
        head -1
}

json_number() {
    echo "$RESPONSE" |
        sed -n "s/.*\"$1\":\([0-9]*\).*/\1/p" |
        head -1
}


# ============================================================
# PHASE 1 - AUTHENTICATION
# ============================================================

print_header "PHASE 1 - AUTHENTICATION"

print_test "GET /users without JWT"

request "$BASE_URL/users"

expect_status "401"


print_test "POST /auth/login - valid credentials"

request \
    -X POST \
    "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{
        \"username\":\"$ADMIN_USER\",
        \"password\":\"$ADMIN_PASS\"
    }"

expect_status "200"

TOKEN=$(json_string "token")

if [ -n "$TOKEN" ]; then
    echo "JWT token successfully extracted."
else
    echo "JWT token extraction failed."
    fail
fi


print_test "GET /users with valid JWT"

request \
    "$BASE_URL/users" \
    -H "Authorization: Bearer $TOKEN"

expect_status "200"


print_test "GET /users with invalid JWT"

request \
    "$BASE_URL/users" \
    -H "Authorization: Bearer invalid.jwt.token"

expect_status "401"


print_test "GET /users with empty Bearer token"

request \
    "$BASE_URL/users" \
    -H "Authorization: Bearer"

expect_status "401"


print_test "Login with wrong password"

request \
    -X POST \
    "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "username":"rajeb",
        "password":"WrongPassword@999"
    }'

expect_status "401"


print_test "Login with non-existent user"

request \
    -X POST \
    "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "username":"does_not_exist_999999",
        "password":"Password@123"
    }'

expect_status "401"


print_test "Login with empty JSON"

request \
    -X POST \
    "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{}'

expect_status "400"


print_test "Login with invalid JSON"

request \
    -X POST \
    "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{invalid-json'

expect_status "400"


# ============================================================
# PHASE 2 - RBAC
# ============================================================

print_header "PHASE 2 - RBAC"

print_test "GET /roles"

request \
    "$BASE_URL/roles" \
    -H "Authorization: Bearer $TOKEN"

expect_status "200"


print_test "GET /permissions"

request \
    "$BASE_URL/permissions" \
    -H "Authorization: Bearer $TOKEN"

expect_status "200"


print_test "POST /users - create temporary user"

request \
    -X POST \
    "$BASE_URL/users" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    --data-urlencode "username=$TEST_USER" \
    --data-urlencode "email=$TEST_EMAIL" \
    --data-urlencode "password=$TEST_PASS" \
    --data-urlencode "fullName=$TEST_FULL_NAME"

echo "HTTP: $HTTP_CODE"
echo "Response: $RESPONSE"

if [ "$HTTP_CODE" = "201" ]; then
    pass
else
    fail
fi

TEST_USER_ID=$(json_number "userId")

echo "Temporary user ID: $TEST_USER_ID"


if [ -n "$TEST_USER_ID" ]; then

    print_test "GET temporary user"

    request \
        "$BASE_URL/users/$TEST_USER_ID" \
        -H "Authorization: Bearer $TOKEN"

    expect_status "200"


    print_test "PUT temporary user"

    request \
        -X PUT \
        "$BASE_URL/users/$TEST_USER_ID" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d "{
            \"email\":\"$TEST_EMAIL\",
            \"fullName\":\"Updated API Test User\",
            \"status\":\"ACTIVE\"
        }"

    if [ "$HTTP_CODE" = "200" ]; then
        pass
    else
        echo "HTTP: $HTTP_CODE"
        echo "Response: $RESPONSE"
        fail
    fi

fi


print_test "POST /roles - create temporary role"

TEST_ROLE="API_TEST_ROLE_$(date +%s)"

request \
    -X POST \
    "$BASE_URL/roles" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    --data-urlencode "roleName=$TEST_ROLE" \
    --data-urlencode "description=Temporary API test role"

echo "HTTP: $HTTP_CODE"
echo "Response: $RESPONSE"

if [ "$HTTP_CODE" = "201" ]; then
    pass
else
    fail
fi

TEST_ROLE_ID=$(json_number "roleId")

echo "Temporary role ID: $TEST_ROLE_ID"


if [ -n "$TEST_ROLE_ID" ]; then

    print_test "GET temporary role"

    request \
        "$BASE_URL/roles/$TEST_ROLE_ID" \
        -H "Authorization: Bearer $TOKEN"

    expect_status "200"


    print_test "PUT temporary role"

    request \
        -X PUT \
        "$BASE_URL/roles/$TEST_ROLE_ID" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        --data-urlencode "roleName=${TEST_ROLE}_UPDATED" \
        --data-urlencode "description=Updated API test role"

    if [ "$HTTP_CODE" = "200" ]; then
        pass
    else
        echo "HTTP: $HTTP_CODE"
        echo "Response: $RESPONSE"
        fail
    fi

fi


# ============================================================
# PHASE 3 - ACCESS REQUEST / APPROVAL / AUDIT
# ============================================================

print_header "PHASE 3 - ACCESS REQUEST / APPROVAL / AUDIT"

print_test "GET /access-requests"

request \
    "$BASE_URL/access-requests" \
    -H "Authorization: Bearer $TOKEN"

expect_status "200"


print_test "GET /approvals"

request \
    "$BASE_URL/approvals" \
    -H "Authorization: Bearer $TOKEN"

expect_status "200"


print_test "GET /audits"

request \
    "$BASE_URL/audits" \
    -H "Authorization: Bearer $TOKEN"

expect_status "200"


# ============================================================
# PHASE 4 - 2FA / PASSWORD / SESSION
# ============================================================

print_header "PHASE 4 - 2FA / PASSWORD / SESSION"

print_test "POST /auth/2fa/setup"

request \
    -X POST \
    "$BASE_URL/auth/2fa/setup" \
    -H "Authorization: Bearer $TOKEN"

if [ "$HTTP_CODE" = "200" ]; then
    pass
elif [ "$HTTP_CODE" = "400" ]; then
    info "2FA may already be configured"
else
    echo "HTTP: $HTTP_CODE"
    echo "Response: $RESPONSE"
    fail
fi


print_test "POST /auth/2fa/enable with invalid code"

request \
    -X POST \
    "$BASE_URL/auth/2fa/enable" \
    -H "Authorization: Bearer $TOKEN" \
    --data-urlencode "code=000000"

if [ "$HTTP_CODE" = "400" ]; then
    pass
else
    echo "HTTP: $HTTP_CODE"
    echo "Response: $RESPONSE"
    fail
fi


print_test "POST /auth/2fa/verify with invalid userId"

request \
    -X POST \
    "$BASE_URL/auth/2fa/verify" \
    --data-urlencode "userId=invalid" \
    --data-urlencode "code=000000"

expect_status "400"


print_test "Change password with wrong old password"

request \
    -X POST \
    "$BASE_URL/auth/change-password" \
    -H "Authorization: Bearer $TOKEN" \
    --data-urlencode "oldPassword=WrongOldPassword@999" \
    --data-urlencode "newPassword=TemporaryNew@123"

if [ "$HTTP_CODE" = "400" ] || [ "$HTTP_CODE" = "401" ]; then
    pass
else
    echo "HTTP: $HTTP_CODE"
    echo "Response: $RESPONSE"
    fail
fi


# ============================================================
# PHASE 5 - REPORTS / SECURITY
# ============================================================

print_header "PHASE 5 - REPORTS / SECURITY"

print_test "GET /reports/summary"

request \
    "$BASE_URL/reports/summary" \
    -H "Authorization: Bearer $TOKEN"

expect_status "200"


print_test "GET /reports/summary with date filter"

TODAY=$(date +%Y-%m-%d)

request \
    "$BASE_URL/reports/summary?startDate=$TODAY&endDate=$TODAY" \
    -H "Authorization: Bearer $TOKEN"

expect_status "200"


print_test "GET /reports/summary with invalid date"

request \
    "$BASE_URL/reports/summary?startDate=invalid-date" \
    -H "Authorization: Bearer $TOKEN"

if [ "$HTTP_CODE" = "400" ]; then
    pass
else
    echo "HTTP: $HTTP_CODE"
    echo "Response: $RESPONSE"
    fail
fi


print_test "GET non-existent user"

request \
    "$BASE_URL/users/999999999" \
    -H "Authorization: Bearer $TOKEN"

if [ "$HTTP_CODE" = "404" ] || [ "$HTTP_CODE" = "400" ]; then
    pass
else
    echo "HTTP: $HTTP_CODE"
    echo "Response: $RESPONSE"
    fail
fi


print_test "GET non-existent role"

request \
    "$BASE_URL/roles/999999999" \
    -H "Authorization: Bearer $TOKEN"

if [ "$HTTP_CODE" = "404" ] || [ "$HTTP_CODE" = "400" ]; then
    pass
else
    echo "HTTP: $HTTP_CODE"
    echo "Response: $RESPONSE"
    fail
fi


# ============================================================
# PHASE 6 - CLEANUP / LOGOUT
# ============================================================

print_header "PHASE 6 - CLEANUP / LOGOUT"

if [ -n "$TEST_USER_ID" ]; then

    print_test "DELETE temporary user"

    request \
        -X DELETE \
        "$BASE_URL/users/$TEST_USER_ID" \
        -H "Authorization: Bearer $TOKEN"

    expect_status "200"


    print_test "GET deleted temporary user"

    request \
        "$BASE_URL/users/$TEST_USER_ID" \
        -H "Authorization: Bearer $TOKEN"

    if [ "$HTTP_CODE" = "404" ]; then
        pass
    else
        echo "HTTP: $HTTP_CODE"
        echo "Response: $RESPONSE"
        fail
    fi

fi


if [ -n "$TEST_ROLE_ID" ]; then

    print_test "DELETE temporary role"

    request \
        -X DELETE \
        "$BASE_URL/roles/$TEST_ROLE_ID" \
        -H "Authorization: Bearer $TOKEN"

    expect_status "200"


    print_test "GET deleted temporary role"

    request \
        "$BASE_URL/roles/$TEST_ROLE_ID" \
        -H "Authorization: Bearer $TOKEN"

    if [ "$HTTP_CODE" = "404" ]; then
        pass
    else
        echo "HTTP: $HTTP_CODE"
        echo "Response: $RESPONSE"
        fail
    fi

fi


print_test "POST /auth/logout"

request \
    -X POST \
    "$BASE_URL/auth/logout" \
    -H "Authorization: Bearer $TOKEN"

expect_status "200"


print_test "GET /users with logged-out JWT"

request \
    "$BASE_URL/users" \
    -H "Authorization: Bearer $TOKEN"

expect_status "401"


print_test "Login again after logout"

request \
    -X POST \
    "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{
        \"username\":\"$ADMIN_USER\",
        \"password\":\"$ADMIN_PASS\"
    }"

expect_status "200"

TOKEN=$(json_string "token")


print_test "Final GET /users with new JWT"

request \
    "$BASE_URL/users" \
    -H "Authorization: Bearer $TOKEN"

expect_status "200"


# ============================================================
# FINAL TEST SUMMARY
# ============================================================

print_header "FINAL TEST SUMMARY"

echo "PASS : $PASS_COUNT"
echo "FAIL : $FAIL_COUNT"
echo "INFO : $INFO_COUNT"

echo
echo "=============================================="

if [ "$FAIL_COUNT" -eq 0 ]; then
    echo "ALL EXECUTED TESTS PASSED"
    echo "=============================================="
    exit 0
else
    echo "SOME TESTS FAILED"
    echo "=============================================="
    exit 1
fi
