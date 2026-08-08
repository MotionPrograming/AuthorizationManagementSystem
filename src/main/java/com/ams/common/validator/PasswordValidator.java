package com.ams.common.validator;

import com.ams.common.constant.SecurityConstants;
import com.ams.common.exception.ValidationException;
import com.ams.common.util.StringUtil;

public final class PasswordValidator {

	private PasswordValidator() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	public static boolean isValid(String password) {
		if (StringUtil.isBlank(password)) {
			return false;
		}

		int length = password.length();

		if (length < SecurityConstants.PASSWORD_MIN_LENGTH || length > SecurityConstants.PASSWORD_MAX_LENGTH) {
			return false;
		}

		boolean hasUppercase = password.matches(".*[A-Z].*");
		boolean hasLowercase = password.matches(".*[a-z].*");
		boolean hasDigit = password.matches(".*\\d.*");
		boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

		return hasUppercase && hasLowercase && hasDigit && hasSpecial;
	}

	public static void validate(String password) {
		if (!isValid(password)) {
			throw new ValidationException("Password must be between " + SecurityConstants.PASSWORD_MIN_LENGTH + " and "
					+ SecurityConstants.PASSWORD_MAX_LENGTH + " characters long, and contain at least "
					+ "one uppercase letter, one lowercase letter, one digit, and one special character.");
		}
	}
}