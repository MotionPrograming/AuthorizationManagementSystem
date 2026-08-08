package com.ams.common.constant;

public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Application Details
    public static final String APPLICATION_NAME = "Authorization Management System";
    public static final String APPLICATION_VERSION = "1.0.0";
    public static final String DEFAULT_ENCODING = "UTF-8";

    // Pagination Constants
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
}