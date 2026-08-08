package com.ams.common.util;

import com.ams.common.exception.ValidationException;

public final class ValidationUtil {

	private ValidationUtil() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	public static void requireNotNull(Object value, String fieldName) {
		if (value == null) {
			throw new ValidationException(fieldName + " must not be null.");
		}
	}

	public static void requireNotBlank(String value, String fieldName) {
		if (StringUtil.isBlank(value)) {
			throw new ValidationException(fieldName + " must not be blank.");
		}
	}

	public static void requirePositive(long value, String fieldName) {
		if (value <= 0) {
			throw new ValidationException(fieldName + " must be greater than zero.");
		}
	}

	public static void requireLength(String value, int min, int max, String fieldName) {
		requireNotBlank(value, fieldName);
		int length = value.trim().length();
		if (length < min || length > max) {
			throw new ValidationException(
					fieldName + " length must be between " + min + " and " + max + " characters.");
		}
	}
}