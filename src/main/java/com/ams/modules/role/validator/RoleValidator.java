package com.ams.modules.role.validator;

import com.ams.common.exception.ValidationException;
import com.ams.modules.role.dto.CreateRoleRequest;

public class RoleValidator {

	public void validateCreateRole(CreateRoleRequest request) {
		if (request == null) {
			throw new ValidationException("Role request data cannot be null.");
		}
		if (request.getRoleName() == null || request.getRoleName().trim().isEmpty()) {
			throw new ValidationException("Role name is required.");
		}
	}
}