package com.ams.modules.accessrequest.entity;

import java.time.LocalDateTime;

public class AccessRequest {
	private Long requestId;
	private Long userId;
	private String requestType;
	private String requestStatus;
	private String requestReason;
	private LocalDateTime createdAt;

	public AccessRequest() {
	}

	public AccessRequest(Long requestId, Long userId, String requestType, String requestStatus, String requestReason,
			LocalDateTime createdAt) {
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

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getRequestReason() {
		return requestReason;
	}

	public void setRequestReason(String requestReason) {
		this.requestReason = requestReason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}