package com.ams.common.constant;

public final class MessageConstants {

    private MessageConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Success Messages
    public static final String SUCCESS = "Operation completed successfully.";
    public static final String CREATED = "Resource created successfully.";
    public static final String UPDATED = "Resource updated successfully.";
    public static final String DELETED = "Resource deleted successfully.";

    // Request & Validation Error Messages
    public static final String INVALID_REQUEST = "Invalid request.";
    public static final String VALIDATION_FAILED = "Validation failed.";
    public static final String INVALID_USERNAME = "Invalid username.";
    public static final String INVALID_EMAIL = "Invalid email address.";
    public static final String INVALID_PASSWORD = "Invalid password.";

    // Authentication & Authorization Messages
    public static final String UNAUTHORIZED = "Authentication required.";
    public static final String FORBIDDEN = "Access denied.";

    // General Error Messages
    public static final String NOT_FOUND = "Resource not found.";
    public static final String INTERNAL_ERROR = "Internal server error.";
}