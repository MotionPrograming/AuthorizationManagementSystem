package com.ams.common.util;

public final class JsonUtil {

	private JsonUtil() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	public static String escape(String value) {
		if (value == null) {
			return null;
		}

		return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b").replace("\f", "\\f")
				.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
	}

	public static String quote(String value) {
		if (value == null) {
			return "null";
		}
		return "\"" + escape(value) + "\"";
	}
}