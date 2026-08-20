package com.ams.modules.approval.entity;

import java.time.LocalDateTime;

public class Approval {
	private Long approvalId;
	private Long requestId;
	private Long approverId;
	private String decision;
	private String comments;
	private LocalDateTime approvedAt;

	public Approval() {
	}

	public Approval(Long approvalId, Long requestId, Long approverId, String decision, String comments,
			LocalDateTime approvedAt) {
		this.approvalId = approvalId;
		this.requestId = requestId;
		this.approverId = approverId;
		this.decision = decision;
		this.comments = comments;
		this.approvedAt = approvedAt;
	}

	public Long getApprovalId() {
		return approvalId;
	}

	public void setApprovalId(Long approvalId) {
		this.approvalId = approvalId;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public Long getApproverId() {
		return approverId;
	}

	public void setApproverId(Long approverId) {
		this.approverId = approverId;
	}

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public LocalDateTime getApprovedAt() {
		return approvedAt;
	}

	public void setApprovedAt(LocalDateTime approvedAt) {
		this.approvedAt = approvedAt;
	}
}