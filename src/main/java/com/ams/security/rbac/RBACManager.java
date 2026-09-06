package com.ams.security.rbac;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.ams.config.DBConnection;

public class RBACManager {

	public Set<String> getUserRoles(Long userId) {
		Set<String> roles = new HashSet<>();
		String sql = "SELECT r.ROLE_NAME FROM ROLES r " + "JOIN USER_ROLES ur ON r.ROLE_ID = ur.ROLE_ID "
				+ "WHERE ur.USER_ID = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, userId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					roles.add(rs.getString("ROLE_NAME"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return roles;
	}

	public Set<String> getUserPermissions(Long userId) {
		Set<String> permissions = new HashSet<>();
		String sql = "SELECT p.PERMISSION_NAME FROM PERMISSIONS p "
				+ "JOIN ROLE_PERMISSIONS rp ON p.PERMISSION_ID = rp.PERMISSION_ID "
				+ "JOIN USER_ROLES ur ON rp.ROLE_ID = ur.ROLE_ID " + "WHERE ur.USER_ID = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, userId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					permissions.add(rs.getString("PERMISSION_NAME"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return permissions;
	}

	public boolean hasRole(Long userId, String roleName) {
		Set<String> roles = getUserRoles(userId);
		return roles.contains(roleName);
	}

	public boolean hasPermission(Long userId, String permissionName) {
		Set<String> permissions = getUserPermissions(userId);
		return permissions.contains(permissionName);
	}
}