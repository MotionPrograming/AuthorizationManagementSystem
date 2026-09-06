package com.ams.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Set;

public class JsonUtil {

	public static String toJson(Object obj) {
		return toJson(obj, Collections.newSetFromMap(new IdentityHashMap<>()));
	}

	private static String toJson(Object obj, Set<Object> visited) {
		if (obj == null) {
			return "null";
		}

		if (visited.contains(obj)) {
			return "null";
		}

		if (obj instanceof String || obj instanceof Character) {
			return "\"" + escapeJson(obj.toString()) + "\"";
		}

		if (obj instanceof Number || obj instanceof Boolean) {
			return obj.toString();
		}

		// Date, LocalDateTime, LocalDate ইত্যাদি টাইপ সরাসরি String হিসেবে রিটার্ন করবে
		if (obj instanceof Date || obj instanceof TemporalAccessor) {
			return "\"" + escapeJson(obj.toString()) + "\"";
		}

		if (obj instanceof Collection<?>) {
			visited.add(obj);
			Collection<?> list = (Collection<?>) obj;
			StringBuilder sb = new StringBuilder("[");
			boolean first = true;
			for (Object item : list) {
				if (!first)
					sb.append(",");
				sb.append(toJson(item, visited));
				first = false;
			}
			sb.append("]");
			return sb.toString();
		}

		return objectToJson(obj, visited);
	}

	private static String objectToJson(Object obj, Set<Object> visited) {
		visited.add(obj);
		StringBuilder sb = new StringBuilder("{");
		Field[] fields = obj.getClass().getDeclaredFields();
		boolean first = true;

		for (Field field : fields) {
			// Static, Transient এবং Synthetic ফিল্ড (যেমন serialVersionUID) স্কিপ করা হচ্ছে
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic()) {
				continue;
			}

			field.setAccessible(true);
			try {
				Object value = field.get(obj);
				if (!first)
					sb.append(",");

				sb.append("\"").append(field.getName()).append("\":");

				if (value == null) {
					sb.append("null");
				} else if (value instanceof String || value instanceof Character) {
					sb.append("\"").append(escapeJson(value.toString())).append("\"");
				} else if (value instanceof Number || value instanceof Boolean) {
					sb.append(value);
				} else if (value instanceof Date || value instanceof TemporalAccessor) {
					sb.append("\"").append(escapeJson(value.toString())).append("\"");
				} else {
					sb.append(toJson(value, visited));
				}

				first = false;
			} catch (IllegalAccessException e) {
				// Ignore inaccessible fields
			}
		}
		sb.append("}");
		return sb.toString();
	}

	private static String escapeJson(String input) {
		if (input == null)
			return "";
		return input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b").replace("\f", "\\f")
				.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
	}
}