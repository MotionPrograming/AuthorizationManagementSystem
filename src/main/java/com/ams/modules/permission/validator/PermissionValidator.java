package com.ams.modules.permission.validator;

import com.ams.common.exception.ValidationException;
import com.ams.modules.permission.dto.CreatePermissionRequest;

public class PermissionValidator {

	public void validateCreatePermission(CreatePermissionRequest request) {
		if (request == null) {
			throw new ValidationException("Permission request data cannot be null.");
		}
		if (request.getPermissionName() == null || request.getPermissionName().trim().isEmpty()) {
			throw new ValidationException("Permission name is required.");
		}
	}
	public void validateUpdatePermission(com.ams.modules.permission.dto.UpdatePermissionRequest request) {
		if (request == null || request.getPermissionName() == null || request.getPermissionName().trim().isEmpty()) throw new ValidationException("Permission name is required.");
	}
}