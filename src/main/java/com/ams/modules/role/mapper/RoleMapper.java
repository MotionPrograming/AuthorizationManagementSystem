package com.ams.modules.role.mapper;

import com.ams.modules.role.dto.RoleResponse;
import com.ams.modules.role.entity.Role;

public class RoleMapper {

	public static RoleResponse toRoleResponse(Role role) {
		if (role == null)
			return null;
		return new RoleResponse(role.getRoleId(), role.getRoleName(), role.getDescription(), role.getCreatedAt());
	}
}