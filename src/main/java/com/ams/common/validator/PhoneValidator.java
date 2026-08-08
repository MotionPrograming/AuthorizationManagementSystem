package com.ams.common.validator;

import com.ams.common.constant.MessageConstants;
import com.ams.common.exception.ValidationException;
import com.ams.common.util.StringUtil;

public final class PhoneValidator {

	private PhoneValidator() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	public static boolean isValid(String phone) {
		if (StringUtil.isBlank(phone)) {
			return false;
		}
		String normalized = phone.replaceAll("[\\s-]", "");
		return normalized.matches("^\\+?[0-9]{10,15}$");
	}

	public static void validate(String phone) {
		if (!isValid(phone)) {
			throw new ValidationException("Invalid phone number");
		}
	}
}