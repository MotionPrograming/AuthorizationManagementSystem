package com.ams.security.twofactor;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import com.ams.security.crypto.HashUtils;

public final class BackupCodeManager {
    private static final int CODE_LENGTH = 10;
    private static final int TOTAL_CODES = 8;
    private static final String ALPHA_NUMERIC = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private BackupCodeManager() { }

    public static List<String> generateBackupCodes() {
        SecureRandom random = new SecureRandom();
        List<String> codes = new ArrayList<>(TOTAL_CODES);
        for (int i = 0; i < TOTAL_CODES; i++) {
            StringBuilder code = new StringBuilder(CODE_LENGTH);
            for (int j = 0; j < CODE_LENGTH; j++)
                code.append(ALPHA_NUMERIC.charAt(random.nextInt(ALPHA_NUMERIC.length())));
            codes.add(code.toString());
        }
        return codes;
    }

    public static String hash(String code) {
        if (code == null) throw new IllegalArgumentException("Backup code cannot be null");
        return HashUtils.hashData(code.trim().toUpperCase());
    }
}
