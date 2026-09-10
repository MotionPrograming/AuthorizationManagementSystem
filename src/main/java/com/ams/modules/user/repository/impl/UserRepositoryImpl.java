package com.ams.modules.user.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ams.config.DBConnection;
import com.ams.modules.user.entity.User;
import com.ams.modules.user.repository.UserRepository;

public class UserRepositoryImpl implements UserRepository {

	@Override
	public Optional<User> findById(Long id) {
		String sql = "SELECT USER_ID, USERNAME, EMAIL, PASSWORD_HASH, FULL_NAME, STATUS, IS_2FA_ENABLED, TWO_FACTOR_SECRET, CREATED_AT, UPDATED_AT, LAST_LOGIN_AT FROM USERS WHERE USER_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToUser(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public Optional<User> findByUsername(String username) {
		String sql = "SELECT USER_ID, USERNAME, EMAIL, PASSWORD_HASH, FULL_NAME, STATUS, IS_2FA_ENABLED, TWO_FACTOR_SECRET, CREATED_AT, UPDATED_AT, LAST_LOGIN_AT FROM USERS WHERE USERNAME = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToUser(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public Optional<User> findByEmail(String email) {
		String sql = "SELECT USER_ID, USERNAME, EMAIL, PASSWORD_HASH, FULL_NAME, STATUS, IS_2FA_ENABLED, TWO_FACTOR_SECRET, CREATED_AT, UPDATED_AT, LAST_LOGIN_AT FROM USERS WHERE EMAIL = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, email);
			try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(mapResultSetToUser(rs)); }
		} catch (SQLException e) { throw new RuntimeException("Failed to find user by email", e); }
		return Optional.empty();
	}

	@Override
	public List<User> findAll() {
		List<User> users = new ArrayList<>();
		String sql = "SELECT USER_ID, USERNAME, EMAIL, PASSWORD_HASH, FULL_NAME, STATUS, IS_2FA_ENABLED, TWO_FACTOR_SECRET, CREATED_AT, UPDATED_AT, LAST_LOGIN_AT FROM USERS";
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				users.add(mapResultSetToUser(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	@Override
	public boolean save(User user) {
		String sql = "INSERT INTO USERS (USERNAME, EMAIL, PASSWORD_HASH, FULL_NAME, STATUS, CREATED_AT, UPDATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, new String[] {"USER_ID"})) {

			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getEmail());
			stmt.setString(3, user.getPasswordHash());
			stmt.setString(4, user.getFullName());
			stmt.setString(5, user.getStatus() != null ? user.getStatus() : "ACTIVE");
			stmt.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));
			stmt.setTimestamp(7, Timestamp.valueOf(user.getUpdatedAt()));

			int affected = stmt.executeUpdate();
			if (affected == 1) {
				try (ResultSet keys = stmt.getGeneratedKeys()) {
					if (keys.next()) user.setUserId(keys.getLong(1));
				}
			}
			return affected == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(User user) {
		String sql = "UPDATE USERS SET EMAIL = ?, FULL_NAME = ?, STATUS = ?, PASSWORD_HASH = ?, IS_2FA_ENABLED = ?, TWO_FACTOR_SECRET = ?, UPDATED_AT = ? WHERE USER_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, user.getEmail());
			stmt.setString(2, user.getFullName());
			stmt.setString(3, user.getStatus());
			stmt.setString(4, user.getPasswordHash());
			if (user.getIs2faEnabled() == null) stmt.setNull(5, java.sql.Types.NUMERIC); else stmt.setInt(5, user.getIs2faEnabled());
			stmt.setString(6, user.getTwoFactorSecret());
			stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
			stmt.setLong(8, user.getUserId());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean updateLastLogin(Long userId) {
		String sql = "UPDATE USERS SET LAST_LOGIN_AT = CURRENT_TIMESTAMP, UPDATED_AT = CURRENT_TIMESTAMP WHERE USER_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, userId);
			return stmt.executeUpdate() == 1;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to update last login time", e);
		}
	}

	@Override
	public boolean deleteById(Long id) {
		String sql = "DELETE FROM USERS WHERE USER_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private User mapResultSetToUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setUserId(rs.getLong("USER_ID"));
		user.setUsername(rs.getString("USERNAME"));
		user.setEmail(rs.getString("EMAIL"));
		user.setPasswordHash(rs.getString("PASSWORD_HASH"));
		user.setFullName(rs.getString("FULL_NAME"));
		user.setStatus(rs.getString("STATUS"));
		int twoFa = rs.getInt("IS_2FA_ENABLED");
		if (!rs.wasNull()) user.setIs2faEnabled(twoFa);
		user.setTwoFactorSecret(rs.getString("TWO_FACTOR_SECRET"));

		Timestamp created = rs.getTimestamp("CREATED_AT");
		if (created != null)
			user.setCreatedAt(created.toLocalDateTime());

		Timestamp updated = rs.getTimestamp("UPDATED_AT");
		if (updated != null)
			user.setUpdatedAt(updated.toLocalDateTime());

		Timestamp lastLogin = rs.getTimestamp("LAST_LOGIN_AT");
		if (lastLogin != null)
			user.setLastLoginAt(lastLogin.toLocalDateTime());

		return user;
	}

	@Override
	public long countUsers() {
		String sql = "SELECT COUNT(*) FROM USERS";
		try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			return rs.next() ? rs.getLong(1) : 0;
		} catch (SQLException e) { throw new RuntimeException("Failed to count users", e); }
	}

	@Override
	public void assignRoleToUser(Long userId, Long roleId) {
		String sql = "INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (?, ?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, userId);
			pstmt.setLong(2, roleId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Error assigning role to user: " + e.getMessage(), e);
		}
	}
}