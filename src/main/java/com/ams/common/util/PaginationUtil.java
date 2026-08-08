package com.ams.common.util;

public final class PaginationUtil {

	private PaginationUtil() {
	}

	public static int normalizePage(int page) {

		return Math.max(page, 1);
	}

	public static int normalizePageSize(int pageSize, int maxPageSize) {

		if (pageSize <= 0) {
			return 10;
		}

		return Math.min(pageSize, maxPageSize);
	}

	public static int calculateOffset(int page, int pageSize) {

		int normalizedPage = normalizePage(page);

		return (normalizedPage - 1) * pageSize;
	}

	public static int calculateTotalPages(int totalRecords, int pageSize) {

		if (totalRecords <= 0 || pageSize <= 0) {
			return 0;
		}

		return (int) Math.ceil((double) totalRecords / pageSize);
	}
}