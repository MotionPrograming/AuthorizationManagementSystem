package com.ams.modules.audit.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ams.config.DBConnection;
import com.ams.modules.audit.entity.AuditLog;
import com.ams.modules.audit.repository.AuditRepository;

public class AuditRepositoryImpl implements AuditRepository {

	@Override
	public Optional<AuditLog> findById(Long auditId) {
		String sql = "SELECT AUDIT_ID, USER_ID, ACTION, DESCRIPTION, IP_ADDRESS, CREATED_AT FROM AUDIT_LOG WHERE AUDIT_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, auditId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToAuditLog(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public List<AuditLog> findByUserId(Long userId) {
		List<AuditLog> logs = new ArrayList<>();
		String sql = "SELECT AUDIT_ID, USER_ID, ACTION, DESCRIPTION, IP_ADDRESS, CREATED_AT FROM AUDIT_LOG WHERE USER_ID = ? ORDER BY CREATED_AT DESC";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, userId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					logs.add(mapResultSetToAuditLog(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return logs;
	}

	@Override
	public List<AuditLog> findAll() {
		List<AuditLog> logs = new ArrayList<>();
		String sql = "SELECT AUDIT_ID, USER_ID, ACTION, DESCRIPTION, IP_ADDRESS, CREATED_AT FROM AUDIT_LOG ORDER BY CREATED_AT DESC";
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				logs.add(mapResultSetToAuditLog(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return logs;
	}

	@Override
	public boolean save(AuditLog auditLog) {
		String sql = "INSERT INTO AUDIT_LOG (USER_ID, ACTION, DESCRIPTION, IP_ADDRESS, CREATED_AT) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			if (auditLog.getUserId() != null) {
				stmt.setLong(1, auditLog.getUserId());
			} else {
				stmt.setNull(1, Types.NUMERIC);
			}
			stmt.setString(2, auditLog.getAction());
			stmt.setString(3, auditLog.getDescription());
			stmt.setString(4, auditLog.getIpAddress());
			stmt.setTimestamp(5, Timestamp.valueOf(auditLog.getCreatedAt()));

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private AuditLog mapResultSetToAuditLog(ResultSet rs) throws SQLException {
		AuditLog auditLog = new AuditLog();
		auditLog.setAuditId(rs.getLong("AUDIT_ID"));

		long userId = rs.getLong("USER_ID");
		if (!rs.wasNull()) {
			auditLog.setUserId(userId);
		}

		auditLog.setAction(rs.getString("ACTION"));
		auditLog.setDescription(rs.getString("DESCRIPTION"));
		auditLog.setIpAddress(rs.getString("IP_ADDRESS"));

		Timestamp created = rs.getTimestamp("CREATED_AT");
		if (created != null) {
			auditLog.setCreatedAt(created.toLocalDateTime());
		}
		return auditLog;
	}
}