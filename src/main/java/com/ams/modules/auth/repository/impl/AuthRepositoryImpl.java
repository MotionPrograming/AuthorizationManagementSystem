package com.ams.modules.auth.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import com.ams.config.DBConnection;
import com.ams.modules.auth.entity.Session;
import com.ams.modules.auth.repository.AuthRepository;

public class AuthRepositoryImpl implements AuthRepository {
    @Override
    public Optional<Session> findSessionByToken(String token) {
        String sql = "SELECT SESSION_ID, USER_ID, TOKEN, LOGIN_TIME, EXPIRY_TIME FROM USER_SESSIONS "
                   + "WHERE TOKEN = ? AND IS_ACTIVE = 1 AND EXPIRY_TIME > CURRENT_TIMESTAMP";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Session s = new Session();
                    s.setId(rs.getLong("SESSION_ID"));
                    s.setUserId(rs.getLong("USER_ID"));
                    s.setSessionToken(rs.getString("TOKEN"));
                    Timestamp created = rs.getTimestamp("LOGIN_TIME");
                    Timestamp expiry = rs.getTimestamp("EXPIRY_TIME");
                    if (created != null) s.setCreatedAt(created.toLocalDateTime());
                    if (expiry != null) s.setExpiresAt(expiry.toLocalDateTime());
                    return Optional.of(s);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to validate session", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean saveSession(Session session) {
        String sql = "INSERT INTO USER_SESSIONS (USER_ID, TOKEN, LOGIN_TIME, EXPIRY_TIME, IS_ACTIVE) VALUES (?, ?, ?, ?, 1)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, session.getUserId());
            stmt.setString(2, session.getSessionToken());
            stmt.setTimestamp(3, Timestamp.valueOf(session.getCreatedAt()));
            stmt.setTimestamp(4, Timestamp.valueOf(session.getExpiresAt()));
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create session", e);
        }
    }

    @Override
    public boolean deleteSession(String token) {
        String sql = "UPDATE USER_SESSIONS SET IS_ACTIVE = 0 WHERE TOKEN = ? AND IS_ACTIVE = 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to invalidate session", e);
        }
    }

    @Override
    public boolean updatePassword(Long userId, String newHashedPassword) {
        String sql = "UPDATE USERS SET PASSWORD_HASH = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE USER_ID = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHashedPassword);
            stmt.setLong(2, userId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update password", e);
        }
    }
}
