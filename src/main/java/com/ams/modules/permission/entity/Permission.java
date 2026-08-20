package com.ams.modules.permission.entity;

import java.time.LocalDateTime;

public class Permission {
	private Long permissionId;
	private String permissionName;
	private String description;
	private LocalDateTime createdAt;

	public Permission() {
	}

	public Permission(Long permissionId, String permissionName, String description, LocalDateTime createdAt) {
		this.permissionId = permissionId;
		this.permissionName = permissionName;
		this.description = description;
		this.createdAt = createdAt;
	}

	public Long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}