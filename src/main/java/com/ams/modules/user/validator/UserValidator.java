package com.ams.modules.user.validator;

import com.ams.common.exception.ValidationException;
import com.ams.modules.user.dto.CreateUserRequest;

public class UserValidator {

	public void validateCreateUser(CreateUserRequest request) {
		if (request == null) {
			throw new ValidationException("User request data cannot be null.");
		}
		if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
			throw new ValidationException("Username is required.");
		}
		if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
			throw new ValidationException("Email is required.");
		}
		if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
			throw new ValidationException("Full name is required.");
		}
		if (request.getPassword() == null || request.getPassword().length() < 6) {
			throw new ValidationException("Password must be at least 6 characters long.");
		}
	}
}