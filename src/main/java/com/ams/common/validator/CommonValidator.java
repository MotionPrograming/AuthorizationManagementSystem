package com.ams.common.validator;

import com.ams.common.util.ValidationUtil;

public final class CommonValidator {

	private CommonValidator() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	public static void validateRequired(String value, String fieldName) {
		ValidationUtil.requiredNotBlank(value, fieldName);
	}

	public static void validateLength(String value, String fieldName, int min, int max) {
		ValidationUtil.requireLength(value, min, max, fieldName);
	}
}