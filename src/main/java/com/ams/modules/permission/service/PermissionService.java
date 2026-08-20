package com.ams.modules.permission.service;

import java.util.List;

import com.ams.modules.permission.dto.CreatePermissionRequest;
import com.ams.modules.permission.dto.PermissionResponse;
import com.ams.modules.permission.dto.UpdatePermissionRequest;

public interface PermissionService {
	PermissionResponse createPermission(CreatePermissionRequest request);

	PermissionResponse getPermissionById(Long permissionId);

	List<PermissionResponse> getAllPermissions();

	boolean updatePermission(Long permissionId, UpdatePermissionRequest request);

	boolean deletePermission(Long permissionId);
}