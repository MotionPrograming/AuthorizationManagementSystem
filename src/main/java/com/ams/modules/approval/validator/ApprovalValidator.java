package com.ams.modules.approval.validator;

import com.ams.common.exception.ValidationException;
import com.ams.modules.approval.dto.CreateApprovalRequest;

public class ApprovalValidator {

	public void validateCreateApproval(CreateApprovalRequest request) {
		if (request == null) {
			throw new ValidationException("Approval request data cannot be null.");
		}
		if (request.getRequestId() == null) {
			throw new ValidationException("Request ID is required.");
		}
		if (request.getApproverId() == null) {
			throw new ValidationException("Approver ID is required.");
		}
		if (request.getDecision() == null
				|| (!request.getDecision().equals("APPROVED") && !request.getDecision().equals("REJECTED"))) {
			throw new ValidationException("Decision must be either 'APPROVED' or 'REJECTED'.");
		}
	}
}