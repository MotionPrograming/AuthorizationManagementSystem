package com.ams.modules.permission.mapper;

import com.ams.modules.permission.dto.PermissionResponse;
import com.ams.modules.permission.entity.Permission;

public class PermissionMapper {

	public static PermissionResponse toPermissionResponse(Permission permission) {
		if (permission == null)
			return null;
		return new PermissionResponse(permission.getPermissionId(), permission.getPermissionName(),
				permission.getDescription(), permission.getCreatedAt());
	}
}