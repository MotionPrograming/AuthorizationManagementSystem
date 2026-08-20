package com.ams.modules.accessrequest.repository.impl;

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
import com.ams.modules.accessrequest.entity.AccessRequest;
import com.ams.modules.accessrequest.repository.AccessRequestRepository;

public class AccessRequestRepositoryImpl implements AccessRequestRepository {

	@Override
	public Optional<AccessRequest> findById(Long requestId) {
		String sql = "SELECT REQUEST_ID, USER_ID, REQUEST_TYPE, REQUEST_STATUS, REQUEST_REASON, CREATED_AT FROM ACCESS_REQUEST WHERE REQUEST_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, requestId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToAccessRequest(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public List<AccessRequest> findByUserId(Long userId) {
		List<AccessRequest> requests = new ArrayList<>();
		String sql = "SELECT REQUEST_ID, USER_ID, REQUEST_TYPE, REQUEST_STATUS, REQUEST_REASON, CREATED_AT FROM ACCESS_REQUEST WHERE USER_ID = ? ORDER BY CREATED_AT DESC";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, userId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					requests.add(mapResultSetToAccessRequest(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return requests;
	}

	@Override
	public List<AccessRequest> findAll() {
		List<AccessRequest> requests = new ArrayList<>();
		String sql = "SELECT REQUEST_ID, USER_ID, REQUEST_TYPE, REQUEST_STATUS, REQUEST_REASON, CREATED_AT FROM ACCESS_REQUEST ORDER BY CREATED_AT DESC";
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				requests.add(mapResultSetToAccessRequest(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return requests;
	}

	@Override
	public boolean save(AccessRequest request) {
		String sql = "INSERT INTO ACCESS_REQUEST (USER_ID, REQUEST_TYPE, REQUEST_STATUS, REQUEST_REASON, CREATED_AT) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, request.getUserId());
			stmt.setString(2, request.getRequestType());
			stmt.setString(3, request.getRequestStatus() != null ? request.getRequestStatus() : "PENDING");
			stmt.setString(4, request.getRequestReason());
			stmt.setTimestamp(5, Timestamp.valueOf(request.getCreatedAt()));

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean updateStatus(Long requestId, String status) {
		String sql = "UPDATE ACCESS_REQUEST SET REQUEST_STATUS = ? WHERE REQUEST_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, status);
			stmt.setLong(2, requestId);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private AccessRequest mapResultSetToAccessRequest(ResultSet rs) throws SQLException {
		AccessRequest request = new AccessRequest();
		request.setRequestId(rs.getLong("REQUEST_ID"));
		request.setUserId(rs.getLong("USER_ID"));
		request.setRequestType(rs.getString("REQUEST_TYPE"));
		request.setRequestStatus(rs.getString("REQUEST_STATUS"));
		request.setRequestReason(rs.getString("REQUEST_REASON"));

		Timestamp created = rs.getTimestamp("CREATED_AT");
		if (created != null) {
			request.setCreatedAt(created.toLocalDateTime());
		}
		return request;
	}
}