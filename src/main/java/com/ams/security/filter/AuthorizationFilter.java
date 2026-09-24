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
			Map.entry("/users:DELETE", "USER_DELETE"),

			Map.entry("/roles:GET", "ROLE_READ"), Map.entry("/roles:POST", "ROLE_CREATE"),
			Map.entry("/roles:PUT", "ROLE_UPDATE"), Map.entry("/roles:DELETE", "ROLE_DELETE"),

			Map.entry("/permissions:GET", "PERMISSION_READ"), Map.entry("/permissions:POST", "PERMISSION_CREATE"),
			Map.entry("/permissions:PUT", "PERMISSION_UPDATE"), Map.entry("/permissions:DELETE", "PERMISSION_DELETE"),

			Map.entry("/access-requests:GET", "ACCESS_REQUEST_READ"),
			Map.entry("/access-requests:POST", "ACCESS_REQUEST_CREATE"),
			Map.entry("/access-requests:PUT", "ACCESS_REQUEST_UPDATE"),

			Map.entry("/approvals:GET", "APPROVAL_READ"), Map.entry("/approvals:POST", "APPROVAL_CREATE"),

			Map.entry("/audits:GET", "AUDIT_READ"), Map.entry("/audits:POST", "AUDIT_CREATE"), // Fixed typo mapping
																								// here

			Map.entry("/reports/summary:GET", "REPORT_READ"),

			Map.entry("/auth/2fa:POST", "2FA_MANAGE"), Map.entry("/auth/change-password:POST", "USER_UPDATE"),
			Map.entry("/auth/logout:POST", "USER_READ"));

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		// 1. Allow public endpoints without authentication/authorization
		if (isPublic(req)) {
			chain.doFilter(request, response);
			return;
		}

		// 2. Get authenticated user from SecurityContext
		UserResponse user = SecurityContextHolder.getContext();
		boolean setHere = false;

		// 3. Fallback authentication if SecurityContext is empty
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

			if (userOpt.isEmpty() || !session.get().getUserId().equals(userOpt.get().getUserId())
					|| !"ACTIVE".equalsIgnoreCase(userOpt.get().getStatus())) {

				deny(resp, 401, "Unauthorized access");
				return;
			}

			user = UserMapper.toUserResponse(userOpt.get());

			SecurityContextHolder.setContext(user);
			setHere = true;
		}

		try {

			// 4. Resolve required permission for the requested route
			String permission = requiredPermission(req);

			// 5. ADMIN bypasses permission checks
			// Other users must have the required permission.
			if (permission != null && !authorizationService.hasRole(user.getUserId(), "ADMIN")
					&& !authorizationService.hasPermission(user.getUserId(), permission)) {

				deny(resp, 403, "Insufficient permission: " + permission);
				return;
			}

			// 6. Continue request
			chain.doFilter(request, response);

		} finally {

			// 7. Clean context if this filter created it
			if (setHere) {
				SecurityContextHolder.clearContext();
			}
		}
	}

	/**
	 * Defines endpoints that do not require authentication or authorization.
	 */
	private boolean isPublic(HttpServletRequest req) {

		String path = req.getRequestURI();
		String method = req.getMethod();

		if (path.endsWith("/auth/login") || path.endsWith("/auth/register") || path.endsWith("/auth/2fa/verify")
				|| path.endsWith("/auth/2fa-login") || path.endsWith("/auth/password/forgot")
				|| path.endsWith("/auth/password/reset")) {

			return true;
		}

		return "POST".equalsIgnoreCase(method) && path.matches(".*/api/v1/users/?$");
	}

	/**
	 * Resolves the required permission for the request.
	 */
	private String requiredPermission(HttpServletRequest req) {

		String uri = req.getRequestURI();

		int index = uri.indexOf("/api/v1");

		String path = index >= 0 ? uri.substring(index + 7) : uri;

		String base = path;

		int slash = path.indexOf('/', 1);

		if (slash > 0 && !path.startsWith("/reports/")) {
			base = path.substring(0, slash);
		}

		return ROUTE_PERMISSIONS.get(base + ":" + req.getMethod());
	}

	/**
	 * Extracts Bearer token from Authorization header.
	 */
	private String extractToken(String header) {

		if (header == null || !header.startsWith("Bearer ")) {

			return null;
		}

		String token = header.substring(7).trim();

		return token.isEmpty() ? null : token;
	}

	/**
	 * Sends JSON error response.
	 */
	private void deny(HttpServletResponse response, int status, String message) throws IOException {

		response.setStatus(status);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		response.getWriter().write(JsonUtil.response("ERROR", message));
	}
}