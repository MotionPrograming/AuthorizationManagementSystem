package com.ams.modules.approval.repository.impl;

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
import com.ams.modules.approval.entity.Approval;
import com.ams.modules.approval.repository.ApprovalRepository;

public class ApprovalRepositoryImpl implements ApprovalRepository {

	@Override
	public Optional<Approval> findById(Long approvalId) {
		String sql = "SELECT APPROVAL_ID, REQUEST_ID, APPROVER_ID, DECISION, COMMENTS, APPROVED_AT FROM APPROVAL WHERE APPROVAL_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, approvalId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToApproval(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public Optional<Approval> findByRequestId(Long requestId) {
		String sql = "SELECT APPROVAL_ID, REQUEST_ID, APPROVER_ID, DECISION, COMMENTS, APPROVED_AT FROM APPROVAL WHERE REQUEST_ID = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, requestId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToApproval(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public List<Approval> findByApproverId(Long approverId) {
		List<Approval> approvals = new ArrayList<>();
		String sql = "SELECT APPROVAL_ID, REQUEST_ID, APPROVER_ID, DECISION, COMMENTS, APPROVED_AT FROM APPROVAL WHERE APPROVER_ID = ? ORDER BY APPROVED_AT DESC";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, approverId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					approvals.add(mapResultSetToApproval(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return approvals;
	}

	@Override
	public List<Approval> findAll() {
		List<Approval> approvals = new ArrayList<>();
		String sql = "SELECT APPROVAL_ID, REQUEST_ID, APPROVER_ID, DECISION, COMMENTS, APPROVED_AT FROM APPROVAL ORDER BY APPROVED_AT DESC";
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				approvals.add(mapResultSetToApproval(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return approvals;
	}

	@Override
	public boolean save(Approval approval) {
		String sql = "INSERT INTO APPROVAL (REQUEST_ID, APPROVER_ID, DECISION, COMMENTS, APPROVED_AT) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, approval.getRequestId());
			stmt.setLong(2, approval.getApproverId());
			stmt.setString(3, approval.getDecision());
			stmt.setString(4, approval.getComments());
			stmt.setTimestamp(5, Timestamp.valueOf(approval.getApprovedAt()));

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private Approval mapResultSetToApproval(ResultSet rs) throws SQLException {
		Approval approval = new Approval();
		approval.setApprovalId(rs.getLong("APPROVAL_ID"));
		approval.setRequestId(rs.getLong("REQUEST_ID"));
		approval.setApproverId(rs.getLong("APPROVER_ID"));
		approval.setDecision(rs.getString("DECISION"));
		approval.setComments(rs.getString("COMMENTS"));

		Timestamp approved = rs.getTimestamp("APPROVED_AT");
		if (approved != null) {
			approval.setApprovedAt(approved.toLocalDateTime());
		}
		return approval;
	}
}