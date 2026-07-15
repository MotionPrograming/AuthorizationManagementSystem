package com.ams.migration;

import com.ams.config.DBConnection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets; 
import java.sql.*;
import java.util.stream.Collectors;

public class MigrationRunner {

    private static final String[] MIGRATIONS = {
        "V001__create_users",
        "V002__create_roles",
        "V003__create_permissions",
        "V004__create_user_roles",
        "V005__create_role_permissions",
        "V006__create_access_request",
        "V007__create_approval",
        "V008__create_user_sessions", 
        "V009__create_password_reset_token",
        "V010__create_audit_log"
    };

    
    // =========================
    // RUN MIGRATION UP
    // =========================
    public void migrate() throws Exception {

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);
            // Create migration tracking table
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("""
                    BEGIN
                        EXECUTE IMMEDIATE '
                        CREATE TABLE schema_migrations (
                            version VARCHAR2(50) PRIMARY KEY,
                            applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        )';
                    EXCEPTION
                        WHEN OTHERS THEN
                            IF SQLCODE != -955 THEN
                                RAISE;
                            END IF;
                    END;
                """);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

            try (PreparedStatement insertPs = conn.prepareStatement("INSERT INTO schema_migrations(version) VALUES (?)")) {
                for (String name : MIGRATIONS) {
                    if (isApplied(conn, name)) {
                        System.out.println("[SKIP] " + name);
                        continue;
                    }
                    
                    System.out.println("[START] " + name);
                    String sqlFile = "db/migration/" + name + ".up.sql";
                    String rawSql = readFile(sqlFile);
                    
                    executeSql(conn, rawSql);
                    
                    insertPs.setString(1, name);
                    insertPs.executeUpdate();
                    conn.commit();
                    
                    System.out.println("[DONE] " + name);
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // =========================
    // RUN ROLLBACK DOWN
    // =========================
    public void rollback() throws Exception {

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);
            
            for (int i = MIGRATIONS.length - 1; i >= 0; i--) {
                String name = MIGRATIONS[i];
                if (!isApplied(conn, name)) {
                    continue;
                }
                
                String path = "db/migration/" + name + ".down.sql";
                InputStream check = getClass().getClassLoader().getResourceAsStream(path);
                
                if (check == null) {
                    System.out.println("[SKIP] No down migration: " + name);
                    continue;
                }
                
                System.out.println("[ROLLBACK] " + name);
                String rawSql = readFile(path);
                executeSql(conn, rawSql);
                
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM schema_migrations WHERE version=?")) {
                    ps.setString(1, name);
                    ps.executeUpdate();
                }
                
                conn.commit();
                System.out.println("[DONE ROLLBACK] " + name);
            }
        }
    }

    // =========================
    // Execute SQL statements
    // =========================
    private void executeSql(Connection conn, String sql) throws SQLException {
        String cleaned = removeComments(sql);
        String[] statements = cleaned.split(";(?=(?:[^']*'[^']*')*[^']*$)");
        
        try (Statement stmt = conn.createStatement()) {
            for (String query : statements) {
                String trimmed = query.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }

    // Remove SQL comments
    private String removeComments(String sql) {
        return sql.replaceAll("(?m)^\\s*--.*$", "")
                  .replaceAll("--.*", "")
                  .replaceAll("/\\*(?s).*?\\*/", "");
    }

    // Check migration already applied
    private boolean isApplied(Connection conn, String version) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM schema_migrations WHERE version=?")) {
            ps.setString(1, version);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Read SQL file
    private String readFile(String path) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new RuntimeException("File not found: " + path);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}