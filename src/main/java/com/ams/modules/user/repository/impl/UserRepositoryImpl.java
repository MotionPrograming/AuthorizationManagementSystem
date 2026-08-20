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
		String sql = "SELECT USER_ID, USERNAME, EMAIL, PASSWORD_HASH, FULL_NAME, STATUS, CREATED_AT, UPDATED_AT, LAST_LOGIN_AT FROM USERS WHERE USER_ID = ?";
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
		String sql = "SELECT USER_ID, USERNAME, EMAIL, PASSWORD_HASH, FULL_NAME, STATUS, CREATED_AT, UPDATED_AT, LAST_LOGIN_AT FROM USERS WHERE USERNAME = ?";
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
	public List<User> findAll() {
		List<User> users = new ArrayList<>();
		String sql = "SELECT USER_ID, USERNAME, EMAIL, PASSWORD_HASH, FULL_NAME, STATUS, CREATED_AT, UPDATED_AT, LAST_LOGIN_AT FROM USERS";
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
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getEmail());
			stmt.setString(3, user.getPasswordHash());
			stmt.setString(4, user.getFullName());
			stmt.setString(5, user.getStatus() != null ? user.getStatus() : "ACTIVE");
			stmt.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));
			stmt.setTimestamp(7, Timestamp.valueOf(user.getUpdatedAt()));

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(User user) {
		String sql = "UPDATE USERS SET EMAIL = ?, FULL_NAME = ?, STATUS = ?, UPDATED_AT = ? WHERE USER_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, user.getEmail());
			stmt.setString(2, user.getFullName());
			stmt.setString(3, user.getStatus());
			stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
			stmt.setLong(5, user.getUserId());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
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
}