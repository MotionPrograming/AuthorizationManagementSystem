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
	public Optional<Session> findSessionByToken(String sessionToken) {
		String sql = "SELECT id, user_id, session_token, created_at, expires_at FROM user_sessions WHERE session_token = ? AND expires_at > NOW()";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, sessionToken);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Session session = new Session();
					session.setId(rs.getLong("id"));
					session.setUserId(rs.getLong("user_id"));
					session.setSessionToken(rs.getString("session_token"));
					session.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
					session.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
					return Optional.of(session);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public boolean saveSession(Session session) {
		String sql = "INSERT INTO user_sessions (user_id, session_token, created_at, expires_at) VALUES (?, ?, ?, ?)";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, session.getUserId());
			stmt.setString(2, session.getSessionToken());
			stmt.setTimestamp(3, Timestamp.valueOf(session.getCreatedAt()));
			stmt.setTimestamp(4, Timestamp.valueOf(session.getExpiresAt()));

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteSession(String sessionToken) {
		String sql = "DELETE FROM user_sessions WHERE session_token = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, sessionToken);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean updatePassword(Long userId, String newHashedPassword) {
		String sql = "UPDATE users SET password_hash = ? WHERE id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, newHashedPassword);
			stmt.setLong(2, userId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}