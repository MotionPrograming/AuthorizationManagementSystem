package com.ams.modules.permission.dto;

public class UpdatePermissionRequest {
	private String permissionName;
	private String description;

	public UpdatePermissionRequest() {
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
}