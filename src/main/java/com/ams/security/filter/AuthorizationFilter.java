package com.ams.security.filter;

import java.io.IOException;
import java.util.Map;

import com.ams.common.util.JsonUtil;
import com.ams.modules.auth.repository.impl.AuthRepositoryImpl;
import com.ams.modules.user.dto.UserResponse;
import com.ams.modules.user.mapper.UserMapper;
import com.ams.modules.user.repository.impl.UserRepositoryImpl;
import com.ams.security.authentication.SecurityContextHolder;
import com.ams.security.authorization.AuthorizationService;
import com.ams.security.crypto.JwtTokenProvider;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/api/v1/*")
public class AuthorizationFilter implements Filter {
	private final AuthorizationService authorizationService = new AuthorizationService();
	private final UserRepositoryImpl users = new UserRepositoryImpl();
	private final AuthRepositoryImpl sessions = new AuthRepositoryImpl();
	private static final Map<String, String> ROUTE_PERMISSIONS = Map.ofEntries(Map.entry("/users:GET", "USER_READ"),
			Map.entry("/users:POST", "USER_CREATE"), Map.entry("/users:PUT", "USER_UPDATE"),
			Map.entry("/users:DELETE", "USER_DELETE"), Map.entry("/roles:GET", "ROLE_READ"),
			Map.entry("/roles:POST", "ROLE_CREATE"), Map.entry("/roles:PUT", "ROLE_UPDATE"),
			Map.entry("/roles:DELETE", "ROLE_DELETE"), Map.entry("/permissions:GET", "PERMISSION_READ"),
			Map.entry("/permissions:POST", "PERMISSION_CREATE"), Map.entry("/permissions:PUT", "PERMISSION_UPDATE"),
			Map.entry("/permissions:DELETE", "PERMISSION_DELETE"),
			Map.entry("/access-requests:GET", "ACCESS_REQUEST_READ"),
			Map.entry("/access-requests:POST", "ACCESS_REQUEST_CREATE"),
			Map.entry("/access-requests:PUT", "ACCESS_REQUEST_UPDATE"), Map.entry("/approvals:GET", "APPROVAL_READ"),
			Map.entry("/approvals:POST", "APPROVAL_CREATE"), Map.entry("/audits:GET", "AUDIT_READ"),
			Map.entry("/audits:POST", "AUDIT_CREATE"), Map.entry("/reports/summary:GET", "REPORT_READ"),
			Map.entry("/auth/2fa:POST", "2FA_MANAGE"), Map.entry("/auth/change-password:POST", "USER_UPDATE"),
			Map.entry("/auth/logout:POST", "USER_READ"));

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		if (isPublic(req)) {
			chain.doFilter(request, response);
			return;
		}
		UserResponse user = SecurityContextHolder.getContext();
		boolean setHere = false;
		if (user == null) {
			String token = extractToken(req.getHeader("Authorization"));
			if (token == null || !JwtTokenProvider.validateToken(token)) {
				deny(resp, 401, "Unauthorized access");
				return;
			}
			var session = sessions.findSessionByToken(token);
			if (session.isEmpty()) {
				deny(resp, 401, "Session is invalid or expired");
				return;
			}
			String username = JwtTokenProvider.getUsernameFromToken(token);
			var userOpt = users.findByUsername(username);
			if (userOpt.isEmpty() || !session.get().getUserId().equals(userOpt.get().getUserId())) {
				deny(resp, 401, "Unauthorized access");
				return;
			}
			user = UserMapper.toUserResponse(userOpt.get());
			SecurityContextHolder.setContext(user);
			setHere = true;
		}
		try {
			String permission = requiredPermission(req);
			if (permission != null && !authorizationService.hasRole(user.getUserId(), "ADMIN")
					&& !authorizationService.hasPermission(user.getUserId(), permission)) {
				deny(resp, 403, "Insufficient permission: " + permission);
				return;
			}
			chain.doFilter(request, response);
		} finally {
			if (setHere)
				SecurityContextHolder.clearContext();
		}
	}

	private String requiredPermission(HttpServletRequest req) {
		String uri = req.getRequestURI();
		int i = uri.indexOf("/api/v1");
		String path = i >= 0 ? uri.substring(i + 7) : uri;
		String base = path;
		int slash = path.indexOf('/', 1);
		if (slash > 0 && !path.startsWith("/reports/"))
			base = path.substring(0, slash);
		return ROUTE_PERMISSIONS.get(base + ":" + req.getMethod());
	}

	private boolean isPublic(HttpServletRequest req) {
		String p = req.getRequestURI();
		return p.endsWith("/auth/login") || p.endsWith("/auth/register") || p.endsWith("/auth/2fa/verify")
				|| p.endsWith("/auth/2fa-login") || p.endsWith("/auth/password/forgot")
				|| p.endsWith("/auth/password/reset");
	}

	private String extractToken(String h) {
		if (h == null || !h.startsWith("Bearer "))
			return null;
		String t = h.substring(7).trim();
		return t.isEmpty() ? null : t;
	}

	private void deny(HttpServletResponse r, int s, String m) throws IOException {
		r.setStatus(s);
		r.setContentType("application/json");
		r.getWriter().write(JsonUtil.response("ERROR", m));
	}
}
