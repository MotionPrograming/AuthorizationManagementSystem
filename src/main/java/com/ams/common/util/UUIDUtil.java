package com.ams.common.util;

import java.util.UUID;

public final class UUIDUtil {

	private UUIDUtil() {
	}

	public static String generate() {
		return UUID.randomUUID().toString();
	}

	public static UUID generateUUID() {
		return UUID.randomUUID();
	}

	public static boolean isValid(String value) {

		if (StringUtil.isBlank(value)) {
			return false;
		}

		try {
			UUID.fromString(value);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}