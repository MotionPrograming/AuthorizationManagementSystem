package com.ams.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    static {

        try {

            Class.forName(DatabaseConfig.getProperty("db.driver"));

        } catch (ClassNotFoundException e) {

            throw new RuntimeException("JDBC Driver loading failed", e);
        }

    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {

        Connection connection = DriverManager.getConnection(
                DatabaseConfig.getProperty("db.url"),
                DatabaseConfig.getProperty("db.username"),
                DatabaseConfig.getProperty("db.password"));

        connection.setAutoCommit(true);

        return connection;
    }

}