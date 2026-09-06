package com.ams.security.twofactor;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class BackupCodeManager {

	private static final int CODE_LENGTH = 8;
	private static final int TOTAL_CODES = 8;
	private static final String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	/**
	 * ৮ সংখ্যার ৮টি ওয়ান-টাইম ইমার্জেন্সি রিকভারি কোড জেনারেট করে
	 */
	public static List<String> generateBackupCodes() {
		List<String> codes = new ArrayList<>();
		SecureRandom random = new SecureRandom();

		for (int i = 0; i < TOTAL_CODES; i++) {
			StringBuilder sb = new StringBuilder(CODE_LENGTH);
			for (int j = 0; j < CODE_LENGTH; j++) {
				sb.append(ALPHA_NUMERIC.charAt(random.nextInt(ALPHA_NUMERIC.length())));
			}
			codes.add(sb.toString());
		}
		return codes;
	}
}