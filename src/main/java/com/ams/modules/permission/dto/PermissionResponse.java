package com.ams.modules.permission.dto;

import java.time.LocalDateTime;

public class PermissionResponse {
	private Long permissionId;
	private String permissionName;
	private String description;
	private LocalDateTime createdAt;

	public PermissionResponse(Long permissionId, String permissionName, String description, LocalDateTime createdAt) {
		this.permissionId = permissionId;
		this.permissionName = permissionName;
		this.description = description;
		this.createdAt = createdAt;
	}

	public Long getPermissionId() {
		return permissionId;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public String getDescription() {
		return description;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}