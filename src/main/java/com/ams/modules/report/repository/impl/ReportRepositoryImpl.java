package com.ams.modules.report.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.ams.config.DBConnection;
import com.ams.modules.report.entity.SystemReportSummary;
import com.ams.modules.report.repository.ReportRepository;

public class ReportRepositoryImpl implements ReportRepository {

	@Override
	public SystemReportSummary getSystemSummary(LocalDate startDate, LocalDate endDate) {
		SystemReportSummary summary = new SystemReportSummary();

		String sql = "SELECT " + "(SELECT COUNT(*) FROM USERS) AS total_users, "
				+ "(SELECT COUNT(*) FROM ACCESS_REQUEST) AS total_requests, "
				+ "(SELECT COUNT(*) FROM ACCESS_REQUEST WHERE REQUEST_STATUS = 'PENDING') AS pending_requests, "
				+ "(SELECT COUNT(*) FROM ACCESS_REQUEST WHERE REQUEST_STATUS = 'APPROVED') AS approved_requests, "
				+ "(SELECT COUNT(*) FROM ACCESS_REQUEST WHERE REQUEST_STATUS = 'REJECTED') AS rejected_requests, "
				+ "(SELECT COUNT(*) FROM AUDIT_LOG) AS total_audits " + "FROM DUAL";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			if (rs.next()) {
				summary.setTotalUsers(rs.getLong("total_users"));
				summary.setTotalAccessRequests(rs.getLong("total_requests"));
				summary.setPendingRequests(rs.getLong("pending_requests"));
				summary.setApprovedRequests(rs.getLong("approved_requests"));
				summary.setRejectedRequests(rs.getLong("rejected_requests"));
				summary.setTotalAuditLogs(rs.getLong("total_audits"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return summary;
	}
}