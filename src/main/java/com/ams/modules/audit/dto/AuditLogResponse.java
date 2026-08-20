package com.ams.modules.audit.dto;

import java.time.LocalDateTime;

public class AuditLogResponse {
	private Long auditId;
	private Long userId;
	private String action;
	private String description;
	private String ipAddress;
	private LocalDateTime createdAt;

	public AuditLogResponse(Long auditId, Long userId, String action, String description, String ipAddress,
			LocalDateTime createdAt) {
		this.auditId = auditId;
		this.userId = userId;
		this.action = action;
		this.description = description;
		this.ipAddress = ipAddress;
		this.createdAt = createdAt;
	}

	public Long getAuditId() {
		return auditId;
	}

	public Long getUserId() {
		return userId;
	}

	public String getAction() {
		return action;
	}

	public String getDescription() {
		return description;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}