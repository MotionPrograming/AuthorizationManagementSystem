package com.ams.modules.role.dto;

import java.time.LocalDateTime;

public class RoleResponse {
	private Long roleId;
	private String roleName;
	private String description;
	private LocalDateTime createdAt;

	public RoleResponse(Long roleId, String roleName, String description, LocalDateTime createdAt) {
		this.roleId = roleId;
		this.roleName = roleName;
		this.description = description;
		this.createdAt = createdAt;
	}

	public Long getRoleId() {
		return roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public String getDescription() {
		return description;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}