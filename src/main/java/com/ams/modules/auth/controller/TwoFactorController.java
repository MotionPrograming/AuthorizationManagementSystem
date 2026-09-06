package com.ams.modules.auth.controller;

import java.io.BufferedReader;
import java.io.IOException;

import com.ams.modules.audit.dto.CreateAuditLogRequest;
import com.ams.modules.audit.repository.impl.AuditRepositoryImpl;
import com.ams.modules.audit.service.AuditService;
import com.ams.modules.audit.service.impl.AuditServiceImpl;
import com.ams.modules.audit.validator.AuditValidator;
import com.ams.modules.user.repository.impl.UserRepositoryImpl;
import com.ams.security.authentication.SecurityContextHolder;
import com.ams.security.twofactor.TwoFactorAuthService;
import com.ams.security.twofactor.TwoFactorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/auth/2fa/*")
public class TwoFactorController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final TwoFactorAuthService twoFactorAuthService;
	private final AuditService auditService;

	public TwoFactorController() {
		this.twoFactorAuthService = new TwoFactorAuthService(new UserRepositoryImpl());
		this.auditService = new AuditServiceImpl(new AuditRepositoryImpl(), new AuditValidator());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		String pathInfo = req.getPathInfo();

		if (pathInfo == null) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"Invalid endpoint\"}");
			return;
		}

		switch (pathInfo) {
		case "/setup":
			handleSetup2FA(req, resp);
			break;
		case "/enable":
			handleEnable2FA(req, resp);
			break;
		case "/verify":
			handleVerify2FA(req, resp);
			break;
		default:
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"Endpoint not found\"}");
			break;
		}
	}

	// 1. /api/v1/auth/2fa/setup - Setup Secret & QR Code
	private void handleSetup2FA(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		var currentUser = SecurityContextHolder.getContext();
		if (currentUser == null) {
			logAudit(null, "SETUP_2FA", req.getRemoteAddr(), "FAILED: Unauthorized setup attempt");
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"Unauthorized access\"}");
			return;
		}

		try {
			TwoFactorResponse response = twoFactorAuthService.setup2FA(currentUser.getUserId());
			logAudit(currentUser.getUserId(), "SETUP_2FA", req.getRemoteAddr(),
					"SUCCESS: Generated 2FA secret and QR code");

			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter()
					.write(String.format("{\"status\": \"SUCCESS\", \"secretKey\": \"%s\", \"qrCodeUrl\": \"%s\"}",
							response.getSecretKey(), response.getQrCodeUrl()));
		} catch (Exception e) {
			logAudit(currentUser.getUserId(), "SETUP_2FA", req.getRemoteAddr(), "FAILED: " + e.getMessage());
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"" + e.getMessage() + "\"}");
		}
	}

	// 2. /api/v1/auth/2fa/enable - Enable 2FA after checking OTP
	private void handleEnable2FA(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		var currentUser = SecurityContextHolder.getContext();
		if (currentUser == null) {
			logAudit(null, "ENABLE_2FA", req.getRemoteAddr(), "FAILED: Unauthorized enable attempt");
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"Unauthorized access\"}");
			return;
		}

		String body = readRequestBody(req);
		int code = extractIntFromJson(body, "code");

		boolean isEnabled = twoFactorAuthService.enable2FA(currentUser.getUserId(), code);

		if (isEnabled) {
			logAudit(currentUser.getUserId(), "ENABLE_2FA", req.getRemoteAddr(),
					"SUCCESS: User enabled 2FA successfully");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write("{\"status\": \"SUCCESS\", \"message\": \"2FA activated successfully\"}");
		} else {
			logAudit(currentUser.getUserId(), "ENABLE_2FA", req.getRemoteAddr(),
					"FAILED: Invalid 2FA OTP code provided");
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"Invalid verification code\"}");
		}
	}

	// 3. /api/v1/auth/2fa/verify - Verify OTP during login flow
	private void handleVerify2FA(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String body = readRequestBody(req);
		long userId = extractLongFromJson(body, "userId");
		int code = extractIntFromJson(body, "code");

		boolean isValid = twoFactorAuthService.authenticate2FA(getSecretByUserId(userId), code);

		if (isValid) {
			logAudit(userId, "VERIFY_2FA_LOGIN", req.getRemoteAddr(), "SUCCESS: 2FA OTP verified during login");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write("{\"status\": \"SUCCESS\", \"message\": \"2FA verification successful\"}");
		} else {
			logAudit(userId, "VERIFY_2FA_LOGIN", req.getRemoteAddr(), "FAILED: Invalid 2FA OTP entered");
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"Invalid 2FA code\"}");
		}
	}

	// Helper to log audit records strictly matching AuditServiceImpl and
	// CreateAuditLogRequest
	private void logAudit(Long userId, String action, String ipAddress, String description) {
		try {
			CreateAuditLogRequest request = new CreateAuditLogRequest();
			request.setUserId(userId);
			request.setAction(action);
			request.setIpAddress(ipAddress);
			request.setDescription(description);

			auditService.logAction(request);
		} catch (Exception e) {
			System.err.println("Failed to write audit log: " + e.getMessage());
		}
	}

	private String readRequestBody(HttpServletRequest req) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = req.getReader()) {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		}
		return sb.toString();
	}

	private int extractIntFromJson(String json, String key) {
		try {
			String searchKey = "\"" + key + "\":";
			int startIndex = json.indexOf(searchKey) + searchKey.length();
			int endIndex = json.indexOf(",", startIndex);
			if (endIndex == -1)
				endIndex = json.indexOf("}", startIndex);
			return Integer.parseInt(json.substring(startIndex, endIndex).replaceAll("[^0-9]", "").trim());
		} catch (Exception e) {
			return 0;
		}
	}

	private long extractLongFromJson(String json, String key) {
		try {
			String searchKey = "\"" + key + "\":";
			int startIndex = json.indexOf(searchKey) + searchKey.length();
			int endIndex = json.indexOf(",", startIndex);
			if (endIndex == -1)
				endIndex = json.indexOf("}", startIndex);
			return Long.parseLong(json.substring(startIndex, endIndex).replaceAll("[^0-9]", "").trim());
		} catch (Exception e) {
			return 0L;
		}
	}

	private String getSecretByUserId(Long userId) {
		UserRepositoryImpl repo = new UserRepositoryImpl();
		return repo.findById(userId).map(user -> user.getTwoFactorSecret()).orElse("");
	}
}