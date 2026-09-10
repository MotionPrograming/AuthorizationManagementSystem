package com.ams.security.twofactor;

import java.util.List;

public class TwoFactorResponse {
    private String secretKey;
    private String qrCodeUrl;
    private List<String> backupCodes;

    public TwoFactorResponse(String secretKey, String qrCodeUrl) { this(secretKey, qrCodeUrl, null); }
    public TwoFactorResponse(String secretKey, String qrCodeUrl, List<String> backupCodes) {
        this.secretKey = secretKey;
        this.qrCodeUrl = qrCodeUrl;
        this.backupCodes = backupCodes;
    }
    public String getSecretKey() { return secretKey; }
    public String getQrCodeUrl() { return qrCodeUrl; }
    public List<String> getBackupCodes() { return backupCodes; }
}
