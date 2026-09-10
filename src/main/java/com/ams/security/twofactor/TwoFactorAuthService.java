package com.ams.security.twofactor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ams.common.exception.ValidationException;
import com.ams.config.DBConnection;
import com.ams.modules.user.entity.User;
import com.ams.modules.user.repository.UserRepository;

public class TwoFactorAuthService {
	private static final String ISSUER = "AMS_Security";
	private final UserRepository userRepository;

	public TwoFactorAuthService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public TwoFactorResponse setup2FA(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new ValidationException("User not found"));
		String secret = TOTPProvider.generateSecretKey();
		String qr = TOTPProvider.getQRCodeUrl(user.getUsername(), ISSUER, secret);
		user.setTwoFactorSecret(secret);
		user.setIs2faEnabled(0);
		if (!userRepository.update(user))
			throw new ValidationException("Unable to save 2FA configuration");
		return new TwoFactorResponse(secret, qr, null);
	}

	public TwoFactorResponse enable2FA(Long userId, int code) {
		User user = getUser(userId);
		if (user.getTwoFactorSecret() == null || !TOTPProvider.validateOTP(user.getTwoFactorSecret(), code))
			return null;
		List<String> backupCodes = BackupCodeManager.generateBackupCodes();
		replaceBackupCodes(userId, backupCodes);
		user.setIs2faEnabled(1);
		if (!userRepository.update(user))
			throw new ValidationException("Unable to enable 2FA");
		return new TwoFactorResponse(null, null, backupCodes);
	}

	public boolean authenticate2FA(String secretKey, int code) {
		return secretKey != null && !secretKey.isEmpty() && TOTPProvider.validateOTP(secretKey, code);
	}

	public boolean authenticateBackupCode(Long userId, String code) {
		if (userId == null || code == null || code.trim().isEmpty())
			return false;
		String sql = "SELECT BACKUP_ID FROM TWO_FACTOR_BACKUP_CODE WHERE USER_ID = ? AND CODE_HASH = ? AND USED = 0";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, userId);
			stmt.setString(2, BackupCodeManager.hash(code));
			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.next())
					return false;
				long id = rs.getLong("BACKUP_ID");
				try (PreparedStatement update = conn.prepareStatement(
						"UPDATE TWO_FACTOR_BACKUP_CODE SET USED = 1, USED_AT = CURRENT_TIMESTAMP WHERE BACKUP_ID = ? AND USED = 0")) {
					update.setLong(1, id);
					return update.executeUpdate() == 1;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Backup code verification failed", e);
		}
	}

	public boolean disable2FA(Long userId, int code) {
		User user = getUser(userId);
		if (user.getTwoFactorSecret() == null || !TOTPProvider.validateOTP(user.getTwoFactorSecret(), code))
			return false;
		user.setIs2faEnabled(0);
		user.setTwoFactorSecret(null);
		deleteBackupCodes(userId);
		return userRepository.update(user);
	}

	private User getUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new ValidationException("User not found"));
	}

	private void replaceBackupCodes(Long userId, List<String> codes) {
		try (Connection conn = DBConnection.getConnection()) {
			try (PreparedStatement del = conn
					.prepareStatement("DELETE FROM TWO_FACTOR_BACKUP_CODE WHERE USER_ID = ?")) {
				del.setLong(1, userId);
				del.executeUpdate();
			}
			try (PreparedStatement ins = conn.prepareStatement(
					"INSERT INTO TWO_FACTOR_BACKUP_CODE (USER_ID, CODE_HASH, USED, CREATED_AT) VALUES (?, ?, 0, CURRENT_TIMESTAMP)")) {
				for (String code : codes) {
					ins.setLong(1, userId);
					ins.setString(2, BackupCodeManager.hash(code));
					ins.addBatch();
				}
				ins.executeBatch();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Unable to store backup codes", e);
		}
	}

	private void deleteBackupCodes(Long userId) {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn
						.prepareStatement("DELETE FROM TWO_FACTOR_BACKUP_CODE WHERE USER_ID = ?")) {
			stmt.setLong(1, userId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Unable to remove backup codes", e);
		}
	}
}
