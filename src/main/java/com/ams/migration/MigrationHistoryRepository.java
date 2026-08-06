package com.ams.migration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MigrationHistoryRepository {

    public void createTableIfNotExists(Connection connection)
            throws SQLException {

        String sql = """
            BEGIN
                EXECUTE IMMEDIATE '
                    CREATE TABLE SCHEMA_MIGRATIONS (
                        VERSION VARCHAR2(50) PRIMARY KEY,
                        DESCRIPTION VARCHAR2(255) NOT NULL,
                        EXECUTION_TIME_MS NUMBER,
                        APPLIED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )';
            EXCEPTION
                WHEN OTHERS THEN
                    IF SQLCODE != -955 THEN
                        RAISE;
                    END IF;
            END;
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public boolean isApplied(Connection connection, String version)
            throws SQLException {

        String sql =
                "SELECT 1 FROM SCHEMA_MIGRATIONS WHERE VERSION=?";

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, version);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next();
            }
        }
    }

    public void save(
            Connection connection,
            Migration migration,
            long executionTime)
            throws SQLException {

        String sql = """
            INSERT INTO SCHEMA_MIGRATIONS
            (
                VERSION,
                DESCRIPTION,
                EXECUTION_TIME_MS
            )
            VALUES
            (
                ?, ?, ?
            )
            """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, migration.getVersion());

            ps.setString(2, migration.getDescription());

            ps.setLong(3, executionTime);

            ps.executeUpdate();
        }
    }

    public void delete(Connection connection, String version)
            throws SQLException {

        String sql =
                "DELETE FROM SCHEMA_MIGRATIONS WHERE VERSION=?";

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, version);

            ps.executeUpdate();
        }
    }

    public List<String> findAll(Connection connection)
            throws SQLException {

        List<String> versions = new ArrayList<>();

        String sql =
                "SELECT VERSION FROM SCHEMA_MIGRATIONS ORDER BY VERSION";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                versions.add(rs.getString("VERSION"));
            }
        }

        return versions;
    }

    public String findLatest(Connection connection)
            throws SQLException {

        String sql = """
            SELECT VERSION
            FROM SCHEMA_MIGRATIONS
            ORDER BY VERSION DESC
            FETCH FIRST 1 ROWS ONLY
            """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getString("VERSION");
            }

            return null;
        }
    }
}