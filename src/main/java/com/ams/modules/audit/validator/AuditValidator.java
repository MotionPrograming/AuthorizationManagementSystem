package com.ams.modules.audit.validator;

import com.ams.common.exception.ValidationException;
import com.ams.modules.audit.dto.CreateAuditLogRequest;

public class AuditValidator {

	public void validateCreateAuditLog(CreateAuditLogRequest request) {
		if (request == null) {
			throw new ValidationException("Audit log request data cannot be null.");
		}
		if (request.getAction() == null || request.getAction().trim().isEmpty()) {
			throw new ValidationException("Action name is required for audit logging.");
		}
	}
}