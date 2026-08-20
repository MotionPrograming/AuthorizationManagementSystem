package com.ams.modules.role.service;

import java.util.List;

import com.ams.modules.role.dto.CreateRoleRequest;
import com.ams.modules.role.dto.RoleResponse;
import com.ams.modules.role.dto.UpdateRoleRequest;

public interface RoleService {
	RoleResponse createRole(CreateRoleRequest request);

	RoleResponse getRoleById(Long roleId);

	List<RoleResponse> getAllRoles();

	boolean updateRole(Long roleId, UpdateRoleRequest request);

	boolean deleteRole(Long roleId);
}