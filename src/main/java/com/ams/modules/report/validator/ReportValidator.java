package com.ams.modules.report.validator;

import com.ams.common.exception.ValidationException;
import com.ams.modules.report.dto.ReportFilterRequest;

public class ReportValidator {

	public void validateFilter(ReportFilterRequest request) {
		if (request != null && request.getStartDate() != null && request.getEndDate() != null) {
			if (request.getStartDate().isAfter(request.getEndDate())) {
				throw new ValidationException("Start date cannot be after end date.");
			}
		}
	}
}