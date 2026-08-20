package com.ams.modules.permission.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ams.config.DBConnection;
import com.ams.modules.permission.entity.Permission;
import com.ams.modules.permission.repository.PermissionRepository;

public class PermissionRepositoryImpl implements PermissionRepository {

	@Override
	public Optional<Permission> findById(Long permissionId) {
		String sql = "SELECT PERMISSION_ID, PERMISSION_NAME, DESCRIPTION, CREATED_AT FROM PERMISSIONS WHERE PERMISSION_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, permissionId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToPermission(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public Optional<Permission> findByPermissionName(String permissionName) {
		String sql = "SELECT PERMISSION_ID, PERMISSION_NAME, DESCRIPTION, CREATED_AT FROM PERMISSIONS WHERE PERMISSION_NAME = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, permissionName);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToPermission(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public List<Permission> findAll() {
		List<Permission> permissions = new ArrayList<>();
		String sql = "SELECT PERMISSION_ID, PERMISSION_NAME, DESCRIPTION, CREATED_AT FROM PERMISSIONS";
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				permissions.add(mapResultSetToPermission(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return permissions;
	}

	@Override
	public boolean save(Permission permission) {
		String sql = "INSERT INTO PERMISSIONS (PERMISSION_NAME, DESCRIPTION, CREATED_AT) VALUES (?, ?, ?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, permission.getPermissionName());
			stmt.setString(2, permission.getDescription());
			stmt.setTimestamp(3, Timestamp.valueOf(permission.getCreatedAt()));

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(Permission permission) {
		String sql = "UPDATE PERMISSIONS SET PERMISSION_NAME = ?, DESCRIPTION = ? WHERE PERMISSION_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, permission.getPermissionName());
			stmt.setString(2, permission.getDescription());
			stmt.setLong(3, permission.getPermissionId());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteById(Long permissionId) {
		String sql = "DELETE FROM PERMISSIONS WHERE PERMISSION_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, permissionId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private Permission mapResultSetToPermission(ResultSet rs) throws SQLException {
		Permission permission = new Permission();
		permission.setPermissionId(rs.getLong("PERMISSION_ID"));
		permission.setPermissionName(rs.getString("PERMISSION_NAME"));
		permission.setDescription(rs.getString("DESCRIPTION"));

		Timestamp created = rs.getTimestamp("CREATED_AT");
		if (created != null) {
			permission.setCreatedAt(created.toLocalDateTime());
		}
		return permission;
	}
}