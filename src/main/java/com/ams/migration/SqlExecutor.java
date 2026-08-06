package com.ams.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class SqlExecutor {

    public void execute(Connection connection, List<String> statements)
            throws SQLException {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null.");
        }

        if (statements == null || statements.isEmpty()) {
            return;
        }

        Instant start = Instant.now();

        boolean originalAutoCommit = connection.getAutoCommit();

        connection.setAutoCommit(false);

        try (Statement statement = connection.createStatement()) {

            for (String sql : statements) {

                if (sql == null || sql.trim().isEmpty()) {
                    continue;
                }

                System.out.println("----------------------------------------");
                System.out.println("[EXECUTING]");
                System.out.println(sql);
                System.out.println("----------------------------------------");

                statement.execute(sql);
            }

            connection.commit();

            Instant end = Instant.now();

            System.out.println(
                    "[SUCCESS] Executed "
                            + statements.size()
                            + " statement(s) in "
                            + Duration.between(start, end).toMillis()
                            + " ms");

        } catch (SQLException ex) {

            connection.rollback();

            System.out.println("[ROLLBACK] Transaction rolled back.");

            throw ex;

        } finally {

            connection.setAutoCommit(originalAutoCommit);
        }
    }
}