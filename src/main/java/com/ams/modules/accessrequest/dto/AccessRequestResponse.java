package com.ams.modules.accessrequest.dto;

import java.time.LocalDateTime;

public class AccessRequestResponse {
	private Long requestId;
	private Long userId;
	private String requestType;
	private String requestStatus;
	private String requestReason;
	private LocalDateTime createdAt;

	public AccessRequestResponse(Long requestId, Long userId, String requestType, String requestStatus,
			String requestReason, LocalDateTime createdAt) {
		this.requestId = requestId;
		this.userId = userId;
		this.requestType = requestType;
		this.requestStatus = requestStatus;
		this.requestReason = requestReason;
		this.createdAt = createdAt;
	}

	public Long getRequestId() {
		return requestId;
	}

	public Long getUserId() {
		return userId;
	}

	public String getRequestType() {
		return requestType;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public String getRequestReason() {
		return requestReason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}