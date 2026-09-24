package com.ams.security.filter;

import java.io.IOException;
import java.util.Optional;

import com.ams.common.util.JsonUtil;
import com.ams.modules.auth.repository.impl.AuthRepositoryImpl;
import com.ams.modules.user.entity.User;
import com.ams.modules.user.mapper.UserMapper;
import com.ams.modules.user.repository.UserRepository;
import com.ams.modules.user.repository.impl.UserRepositoryImpl;
import com.ams.security.authentication.SecurityContextHolder;
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
public class AuthenticationFilter implements Filter {

	private final UserRepository users = new UserRepositoryImpl();
	private final AuthRepositoryImpl sessions = new AuthRepositoryImpl();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		// 1. Public endpoints
		if (isPublic(req)) {
			chain.doFilter(request, response);
			return;
		}

		// 2. Extract and validate JWT
		String token = extractToken(req.getHeader("Authorization"));

		if (token == null || !JwtTokenProvider.validateToken(token)) {
			unauthorized(resp, "Missing or invalid authentication token");
			return;
		}

		// 3. Validate session
		Optional<com.ams.modules.auth.entity.Session> session = sessions.findSessionByToken(token);

		if (session.isEmpty()) {
			unauthorized(resp, "Session is invalid, expired, or logged out");
			return;
		}

		// 4. Validate authenticated user
		String username = JwtTokenProvider.getUsernameFromToken(token);

		Optional<User> user = users.findByUsername(username);

		if (user.isEmpty() || !session.get().getUserId().equals(user.get().getUserId())
				|| !"ACTIVE".equalsIgnoreCase(user.get().getStatus())) {

			unauthorized(resp, "Authenticated user is not active");
			return;
		}

		// 5. Set security context
		try {
			SecurityContextHolder.setContext(UserMapper.toUserResponse(user.get()));

			chain.doFilter(request, response);

		} catch (Exception e) {
			System.err.println("Authentication error: " + e.getMessage());
			unauthorized(resp, "Authentication failed: " + e.getMessage());

		} finally {
			SecurityContextHolder.clearContext();
		}
	}

	/**
	 * Endpoints that do not require authentication.
	 */
	private boolean isPublic(HttpServletRequest req) {

		String path = req.getRequestURI();
		String method = req.getMethod();

		// Authentication endpoints
		if (path.endsWith("/auth/login") || path.endsWith("/auth/register") || path.endsWith("/auth/2fa/verify")
				|| path.endsWith("/auth/2fa-login") || path.endsWith("/auth/password/forgot")
				|| path.endsWith("/auth/password/reset")) {

			return true;
		}

		// User Sign Up
		// Only POST /api/v1/users is public.
		return "POST".equalsIgnoreCase(method) && path.matches(".*/api/v1/users/?$");
	}

	/**
	 * Extract Bearer token from Authorization header.
	 */
	private String extractToken(String header) {

		if (header == null || !header.startsWith("Bearer ")) {
			return null;
		}

		String token = header.substring(7).trim();

		return token.isEmpty() ? null : token;
	}

	/**
	 * Send 401 Unauthorized response.
	 */
	private void unauthorized(HttpServletResponse response, String message) throws IOException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		response.getWriter().write(JsonUtil.response("ERROR", message));
	}
}