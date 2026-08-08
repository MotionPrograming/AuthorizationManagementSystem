package com.ams.common.constant;

public final class SecurityConstants {

    private SecurityConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Security Headers & Tokens
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "Bearer";

    // Session Attributes
    public static final String SESSION_USER_ID = "USER_ID";
    public static final String SESSION_USERNAME = "USERNAME";
    public static final String SESSION_ROLE = "ROLE";

    // Security & Validation Rules
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 100;
    public static final int SESSION_TIMEOUT_SECONDS = 1800; // 30 mins
}