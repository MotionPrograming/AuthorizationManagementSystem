package com.ams.common.constant;

public final class DatabaseConstants{
	private DatabaseConstants() {}
	public static final String JDBC_DRIVER = "oracle.jdbc.OracleDriver";

    public static final String DEFAULT_HOST = "localhost";
    public static final String DEFAULT_PORT = "1521";
    public static final String DEFAULT_SERVICE = "XEPDB1";

    public static final String USERS_TABLE = "USERS";
    public static final String ROLES_TABLE = "ROLES";
    public static final String PERMISSIONS_TABLE = "PERMISSIONS";
    public static final String USER_ROLES_TABLE = "USER_ROLES";
    public static final String ROLE_PERMISSIONS_TABLE = "ROLE_PERMISSIONS";
    public static final String ACCESS_REQUEST_TABLE = "ACCESS_REQUEST";
    public static final String APPROVAL_TABLE = "APPROVAL";
    public static final String USER_SESSIONS_TABLE = "USER_SESSIONS";
    public static final String PASSWORD_RESET_TOKEN_TABLE = "PASSWORD_RESET_TOKEN";
    public static final String AUDIT_LOG_TABLE = "AUDIT_LOG";
    public static final String SCHEMA_MIGRATIONS_TABLE = "SCHEMA_MIGRATIONS";
}