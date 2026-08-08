package com.ams.common.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtil {

	private FileUtil() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	public static String readFile(Path path) {
		if (path == null || !exists(path)) {
			throw new IllegalArgumentException("File path does not exist: " + path);
		}
		try {
			return Files.readString(path);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read file: " + path, e);
		}
	}

	public static void writeFile(Path path, String content) {
		if (path == null) {
			throw new IllegalArgumentException("Path cannot be null");
		}
		try {
			if (path.getParent() != null && !Files.exists(path.getParent())) {
				Files.createDirectories(path.getParent());
			}
			Files.writeString(path, content != null ? content : "");
		} catch (IOException e) {
			throw new RuntimeException("Failed to write file: " + path, e);
		}
	}

	public static boolean exists(Path path) {
		return path != null && Files.exists(path);
	}
}