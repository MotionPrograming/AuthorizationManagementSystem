package com.ams.modules.role.repository.impl;

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
import com.ams.modules.role.entity.Role;
import com.ams.modules.role.repository.RoleRepository;

public class RoleRepositoryImpl implements RoleRepository {

	@Override
	public Optional<Role> findById(Long roleId) {
		String sql = "SELECT ROLE_ID, ROLE_NAME, DESCRIPTION, CREATED_AT FROM ROLES WHERE ROLE_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, roleId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToRole(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public Optional<Role> findByRoleName(String roleName) {
		String sql = "SELECT ROLE_ID, ROLE_NAME, DESCRIPTION, CREATED_AT FROM ROLES WHERE ROLE_NAME = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, roleName);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToRole(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public List<Role> findAll() {
		List<Role> roles = new ArrayList<>();
		String sql = "SELECT ROLE_ID, ROLE_NAME, DESCRIPTION, CREATED_AT FROM ROLES";
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				roles.add(mapResultSetToRole(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return roles;
	}

	@Override
	public boolean save(Role role) {
		String sql = "INSERT INTO ROLES (ROLE_NAME, DESCRIPTION, CREATED_AT) VALUES (?, ?, ?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, role.getRoleName());
			stmt.setString(2, role.getDescription());
			stmt.setTimestamp(3, Timestamp.valueOf(role.getCreatedAt()));

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(Role role) {
		String sql = "UPDATE ROLES SET ROLE_NAME = ?, DESCRIPTION = ? WHERE ROLE_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, role.getRoleName());
			stmt.setString(2, role.getDescription());
			stmt.setLong(3, role.getRoleId());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteById(Long roleId) {
		String sql = "DELETE FROM ROLES WHERE ROLE_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, roleId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private Role mapResultSetToRole(ResultSet rs) throws SQLException {
		Role role = new Role();
		role.setRoleId(rs.getLong("ROLE_ID"));
		role.setRoleName(rs.getString("ROLE_NAME"));
		role.setDescription(rs.getString("DESCRIPTION"));

		Timestamp created = rs.getTimestamp("CREATED_AT");
		if (created != null) {
			role.setCreatedAt(created.toLocalDateTime());
		}
		return role;
	}
}