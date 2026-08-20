package com.ams.modules.report.entity;

public class SystemReportSummary {
	private long totalUsers;
	private long totalAccessRequests;
	private long pendingRequests;
	private long approvedRequests;
	private long rejectedRequests;
	private long totalAuditLogs;

	public SystemReportSummary() {
	}

	public SystemReportSummary(long totalUsers, long totalAccessRequests, long pendingRequests, long approvedRequests,
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

	public void setTotalUsers(long totalUsers) {
		this.totalUsers = totalUsers;
	}

	public long getTotalAccessRequests() {
		return totalAccessRequests;
	}

	public void setTotalAccessRequests(long totalAccessRequests) {
		this.totalAccessRequests = totalAccessRequests;
	}

	public long getPendingRequests() {
		return pendingRequests;
	}

	public void setPendingRequests(long pendingRequests) {
		this.pendingRequests = pendingRequests;
	}

	public long getApprovedRequests() {
		return approvedRequests;
	}

	public void setApprovedRequests(long approvedRequests) {
		this.approvedRequests = approvedRequests;
	}

	public long getRejectedRequests() {
		return rejectedRequests;
	}

	public void setRejectedRequests(long rejectedRequests) {
		this.rejectedRequests = rejectedRequests;
	}

	public long getTotalAuditLogs() {
		return totalAuditLogs;
	}

	public void setTotalAuditLogs(long totalAuditLogs) {
		this.totalAuditLogs = totalAuditLogs;
	}
}