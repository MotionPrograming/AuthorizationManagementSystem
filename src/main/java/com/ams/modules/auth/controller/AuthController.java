package com.ams.modules.auth.controller;

import java.io.IOException;

import com.ams.common.util.JsonUtil;
import com.ams.modules.auth.dto.LoginRequest;
import com.ams.modules.user.dto.CreateUserRequest;
import com.ams.modules.auth.dto.LoginResponse;
import com.ams.modules.auth.repository.impl.AuthRepositoryImpl;
import com.ams.modules.auth.service.AuthService;
import com.ams.modules.auth.service.impl.AuthServiceImpl;
import com.ams.modules.auth.validator.AuthValidator;
import com.ams.modules.user.dto.UserResponse;
import com.ams.modules.user.repository.impl.UserRepositoryImpl;
import com.ams.modules.user.service.UserService;
import com.ams.modules.role.repository.impl.RoleRepositoryImpl;
import com.ams.modules.user.service.impl.UserServiceImpl;
import com.ams.modules.user.validator.UserValidator;
import com.ams.security.authentication.SecurityContextHolder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/auth/*")
public class AuthController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AuthService authService;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        UserRepositoryImpl users = new UserRepositoryImpl();
        this.authService = new AuthServiceImpl(new AuthRepositoryImpl(), users, new AuthValidator());
        this.userService = new UserServiceImpl(users, new UserValidator(), new RoleRepositoryImpl());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        try {
            if ("/login".equals(path)) { login(req, resp); return; }
            if ("/register".equals(path)) { register(req, resp); return; }
            if ("/logout".equals(path)) { logout(req, resp); return; }
            if ("/change-password".equals(path)) { changePassword(req, resp); return; }
            if ("/2fa-login".equals(path)) { twoFactorLogin(req, resp); return; }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(JsonUtil.response("ERROR", "Endpoint not found"));
        } catch (com.ams.common.exception.ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.response("ERROR", e.getMessage()));
        } catch (com.ams.common.exception.AuthenticationException e) {
            String msg = e.getMessage() == null ? "Authentication failed" : e.getMessage();
            resp.setStatus(msg.startsWith("2FA_REQUIRED:") ? 428 : HttpServletResponse.SC_UNAUTHORIZED);
            if (msg.startsWith("2FA_REQUIRED:")) {
                String userId = msg.substring("2FA_REQUIRED:".length());
                resp.getWriter().write("{\"status\":\"2FA_REQUIRED\",\"userId\":" + userId + "}");
            } else resp.getWriter().write(JsonUtil.response("ERROR", msg));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonUtil.response("ERROR", "Internal server error"));
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginRequest request = new LoginRequest();
        request.setUsername(req.getParameter("username"));
        request.setPassword(req.getParameter("password"));
        LoginResponse response = authService.login(request);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(JsonUtil.toJson(response));
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(req.getParameter("username"));
        request.setEmail(req.getParameter("email"));
        request.setPassword(req.getParameter("password"));
        request.setFullName(req.getParameter("fullName"));
        UserResponse response = userService.createUser(request);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(JsonUtil.toJson(response));
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String header = req.getHeader("Authorization");
        if (!authService.logout(header)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonUtil.response("ERROR", "Invalid or expired session"));
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(JsonUtil.response("SUCCESS", "Logout successful"));
    }

    private void changePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var user = SecurityContextHolder.getContext();
        if (user == null) { resp.setStatus(401); resp.getWriter().write(JsonUtil.response("ERROR", "Unauthorized")); return; }
        authService.changePassword(user.getUserId(), req.getParameter("oldPassword"), req.getParameter("newPassword"));
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(JsonUtil.response("SUCCESS", "Password changed successfully"));
    }

    private void twoFactorLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = parseLong(req.getParameter("userId"));
        Integer code = parseCode(req.getParameter("code"));
        LoginResponse response = authService.completeTwoFactorLogin(userId, code);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(JsonUtil.toJson(response));
    }

    private Long parseLong(String value) { try { return Long.valueOf(value); } catch (Exception e) { throw new com.ams.common.exception.ValidationException("userId is required"); } }
    private Integer parseCode(String value) { try { int n = Integer.parseInt(value); if (n < 0 || n > 999999) throw new Exception(); return n; } catch (Exception e) { throw new com.ams.common.exception.ValidationException("A valid 6-digit code is required"); } }
}
