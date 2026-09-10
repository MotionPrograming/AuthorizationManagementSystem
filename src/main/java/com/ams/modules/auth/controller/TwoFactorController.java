package com.ams.modules.auth.controller;

import java.io.IOException;

import com.ams.common.util.JsonUtil;
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
    private TwoFactorAuthService service;

    @Override public void init() throws ServletException { service = new TwoFactorAuthService(new UserRepositoryImpl()); }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json"); resp.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        try {
            if ("/setup".equals(path)) {
                Long uid = currentUserId(resp); if (uid == null) return;
                TwoFactorResponse r = service.setup2FA(uid);
                resp.setStatus(200); resp.getWriter().write(JsonUtil.toJson(r)); return;
            }
            if ("/enable".equals(path)) {
                Long uid = currentUserId(resp); if (uid == null) return;
                TwoFactorResponse r = service.enable2FA(uid, parseCode(req.getParameter("code")));
                if (r == null) { resp.setStatus(400); resp.getWriter().write(JsonUtil.response("ERROR", "Invalid verification code")); return; }
                resp.setStatus(200); resp.getWriter().write(JsonUtil.toJson(r)); return;
            }
            if ("/disable".equals(path)) {
                Long uid = currentUserId(resp); if (uid == null) return;
                boolean ok = service.disable2FA(uid, parseCode(req.getParameter("code")));
                resp.setStatus(ok ? 200 : 400); resp.getWriter().write(JsonUtil.response(ok ? "SUCCESS" : "ERROR", ok ? "2FA disabled" : "Invalid verification code")); return;
            }
            if ("/verify".equals(path)) {
                Long uid = parseLong(req.getParameter("userId"));
                int code = parseCode(req.getParameter("code"));
                boolean ok = service.authenticate2FA(getSecret(uid), code);
                if (!ok && req.getParameter("backupCode") != null) ok = service.authenticateBackupCode(uid, req.getParameter("backupCode"));
                resp.setStatus(ok ? 200 : 401); resp.getWriter().write(JsonUtil.response(ok ? "SUCCESS" : "ERROR", ok ? "2FA verification successful" : "Invalid 2FA code")); return;
            }
            resp.setStatus(404); resp.getWriter().write(JsonUtil.response("ERROR", "Endpoint not found"));
        } catch (com.ams.common.exception.ValidationException e) {
            resp.setStatus(400); resp.getWriter().write(JsonUtil.response("ERROR", e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(500); resp.getWriter().write(JsonUtil.response("ERROR", "2FA operation failed"));
        }
    }

    private Long currentUserId(HttpServletResponse resp) throws IOException {
        var user = SecurityContextHolder.getContext();
        if (user == null) { resp.setStatus(401); resp.getWriter().write(JsonUtil.response("ERROR", "Unauthorized")); return null; }
        return user.getUserId();
    }
    private String getSecret(Long id) { return new UserRepositoryImpl().findById(id).map(u -> u.getTwoFactorSecret()).orElse(""); }
    private Long parseLong(String v) { try { return Long.valueOf(v); } catch (Exception e) { throw new com.ams.common.exception.ValidationException("userId is required"); } }
    private int parseCode(String v) { try { int n = Integer.parseInt(v); if (n < 0 || n > 999999) throw new Exception(); return n; } catch (Exception e) { throw new com.ams.common.exception.ValidationException("A valid 6-digit code is required"); } }
}
