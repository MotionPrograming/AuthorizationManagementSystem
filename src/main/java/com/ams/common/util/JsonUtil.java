
package com.ams.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

/**
 * Small JSON utility used by the servlet API.
 *
 * Responsibilities: - Serialize Java objects to JSON. - Deserialize JSON
 * request bodies to Java objects.
 */
public final class JsonUtil {

	private static final Gson GSON = new Gson();

	private JsonUtil() {
	}

	public static String toJson(Object obj) {
		return toJson(obj, Collections.newSetFromMap(new IdentityHashMap<>()));
	}

	/**
	 * Deserialize a JSON string into the requested Java type.
	 */
	public static <T> T fromJson(String json, Class<T> type) {
		if (json == null || json.isBlank()) {
			throw new IllegalArgumentException("Request body is required.");
		}

		try {
			return GSON.fromJson(json, type);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid JSON request body.");
		}
	}

	public static String escape(String value) {
		if (value == null)
			return "";

		return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b").replace("\f", "\\f")
				.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
	}

	public static String response(String status, String message) {
		return "{\"status\":" + toJson(status) + ",\"message\":" + toJson(message) + "}";
	}

	public static String toJson(Object obj, Set<Object> visited) {
		if (obj == null)
			return "null";

		if (obj instanceof String || obj instanceof Character || obj instanceof Enum<?>)
			return "\"" + escape(obj.toString()) + "\"";

		if (obj instanceof Number || obj instanceof Boolean)
			return obj.toString();

		if (obj instanceof Date || obj instanceof TemporalAccessor)
			return "\"" + escape(obj.toString()) + "\"";

		if (obj instanceof Map<?, ?> map) {
			if (!visited.add(obj))
				return "null";

			StringBuilder sb = new StringBuilder("{");
			boolean first = true;

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				if (!first)
					sb.append(',');

				sb.append(toJson(String.valueOf(entry.getKey()))).append(':').append(toJson(entry.getValue(), visited));

				first = false;
			}

			return sb.append('}').toString();
		}

		if (obj.getClass().isArray()) {
			if (!visited.add(obj))
				return "null";

			StringBuilder sb = new StringBuilder("[");
			int length = java.lang.reflect.Array.getLength(obj);

			for (int i = 0; i < length; i++) {
				if (i > 0)
					sb.append(',');

				sb.append(toJson(java.lang.reflect.Array.get(obj, i), visited));
			}

			return sb.append(']').toString();
		}

		if (obj instanceof Collection<?> collection) {
			if (!visited.add(obj))
				return "null";

			StringBuilder sb = new StringBuilder("[");
			boolean first = true;

			for (Object item : collection) {
				if (!first)
					sb.append(',');

				sb.append(toJson(item, visited));
				first = false;
			}

			return sb.append(']').toString();
		}

		if (!visited.add(obj))
			return "null";

		StringBuilder sb = new StringBuilder("{");
		boolean first = true;

		for (Field field : obj.getClass().getDeclaredFields()) {
			int modifiers = field.getModifiers();

			if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic())
				continue;

			try {
				field.setAccessible(true);

				if (!first)
					sb.append(',');

				sb.append(toJson(field.getName())).append(':').append(toJson(field.get(obj), visited));

				first = false;

			} catch (IllegalAccessException | RuntimeException ignored) {
			}
		}

		return sb.append('}').toString();
	}
}
