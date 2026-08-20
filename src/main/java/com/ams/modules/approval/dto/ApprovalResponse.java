package com.ams.modules.approval.dto;

import java.time.LocalDateTime;

public class ApprovalResponse {
	private Long approvalId;
	private Long requestId;
	private Long approverId;
	private String decision;
	private String comments;
	private LocalDateTime approvedAt;

	public ApprovalResponse(Long approvalId, Long requestId, Long approverId, String decision, String comments,
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

	public Long getRequestId() {
		return requestId;
	}

	public Long getApproverId() {
		return approverId;
	}

	public String getDecision() {
		return decision;
	}

	public String getComments() {
		return comments;
	}

	public LocalDateTime getApprovedAt() {
		return approvedAt;
	}
}