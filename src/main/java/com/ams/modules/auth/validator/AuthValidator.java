package com.ams.modules.auth.validator;

import com.ams.common.exception.ValidationException;
import com.ams.modules.auth.dto.LoginRequest;

public class AuthValidator {

	public void validateLogin(LoginRequest request) {
		if (request == null) {
			throw new ValidationException("Request body cannot be null.");
		}
		if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
			throw new ValidationException("Username is required.");
		}
		if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
			throw new ValidationException("Password is required.");
		}
	}
}