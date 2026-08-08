package com.ams.common.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtil {

	private DateUtil() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);

	public static Timestamp nowTimestamp() {
		return Timestamp.valueOf(LocalDateTime.now());
	}

	public static LocalDateTime now() {
		return LocalDateTime.now();
	}

	public static String format(LocalDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return dateTime.format(DEFAULT_FORMATTER);
	}

	public static LocalDateTime parse(String value) {
		if (StringUtil.isBlank(value)) {
			return null;
		}
		return LocalDateTime.parse(value.trim(), DEFAULT_FORMATTER);
	}
}