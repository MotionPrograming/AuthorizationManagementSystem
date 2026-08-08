package com.ams.common.util;

public final class StringUtil {

	private StringUtil() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	public static boolean isNull(String value) {
		return value == null;
	}

	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

	public static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	public static String trim(String value) {
		if (value == null) {
			return null;
		}
		return value.trim();
	}

	public static String defaultBlank(String value, String defaultValue) {
		return isBlank(value) ? defaultValue : value;
	}
}