package com.ams.common.validator;

import com.ams.common.constant.MessageConstants;
import com.ams.common.exception.ValidationException;
import com.ams.common.util.StringUtil;
import java.util.regex.Pattern;

public final class EmailValidator {

	private EmailValidator() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

	public static boolean isValid(String email) {
		if (StringUtil.isBlank(email)) {
			return false;
		}

		return EMAIL_PATTERN.matcher(email.trim()).matches();
	}

	public static void validate(String email) {
		if (!isValid(email)) {
			throw new ValidationException("Invalid email address.");
		}
	}
}