package com.ams.modules.accessrequest.validator;

import com.ams.common.exception.ValidationException;
import com.ams.modules.accessrequest.dto.AccessRequestRequest;

public class AccessRequestValidator {

	public void validate(AccessRequestRequest request) {
		if (request == null) {
			throw new ValidationException("Access request data cannot be null.");
		}
		if (request.getUserId() == null) {
			throw new ValidationException("User ID is required.");
		}
		if (request.getRequestType() == null || request.getRequestType().trim().isEmpty()) {
			throw new ValidationException("Request type is required.");
		}
	}
}