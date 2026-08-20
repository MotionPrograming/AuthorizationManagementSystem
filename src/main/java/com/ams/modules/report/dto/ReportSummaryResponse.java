package com.ams.modules.report.dto;

public class ReportSummaryResponse {
	private long totalUsers;
	private long totalAccessRequests;
	private long pendingRequests;
	private long approvedRequests;
	private long rejectedRequests;
	private long totalAuditLogs;

	public ReportSummaryResponse(long totalUsers, long totalAccessRequests, long pendingRequests, long approvedRequests,
			long rejectedRequests, long totalAuditLogs) {
		this.totalUsers = totalUsers;
		this.totalAccessRequests = totalAccessRequests;
		this.pendingRequests = pendingRequests;
		this.approvedRequests = approvedRequests;
		this.rejectedRequests = rejectedRequests;
		this.totalAuditLogs = totalAuditLogs;
	}

	public long getTotalUsers() {
		return totalUsers;
	}

	public long getTotalAccessRequests() {
		return totalAccessRequests;
	}

	public long getPendingRequests() {
		return pendingRequests;
	}

	public long getApprovedRequests() {
		return approvedRequests;
	}

	public long getRejectedRequests() {
		return rejectedRequests;
	}

	public long getTotalAuditLogs() {
		return totalAuditLogs;
	}
}