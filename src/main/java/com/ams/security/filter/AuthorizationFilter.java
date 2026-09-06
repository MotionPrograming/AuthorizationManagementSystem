package com.ams.security.filter;

import java.io.IOException;

import com.ams.modules.user.dto.UserResponse;
import com.ams.security.authentication.SecurityContextHolder;
import com.ams.security.authorization.AuthorizationService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/api/v1/protected/*")
public class AuthorizationFilter implements Filter {

	private final AuthorizationService authorizationService;

	public AuthorizationFilter() {
		this.authorizationService = new AuthorizationService();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletResponse resp = (HttpServletResponse) response;

		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		// SecurityContextHolder থেকে সরাসরি UserResponse অবজেক্ট নেয়া
		UserResponse currentUser = SecurityContextHolder.getContext();

		if (currentUser == null) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"Unauthorized access. Please log in.\"}");
			return;
		}

		// Direct User ID Extraction
		Long userId = currentUser.getUserId();

		// RBAC Permission Validation Check
		boolean hasAccess = authorizationService.hasPermission(userId, "READ_PRIVILEGE");

		if (!hasAccess) {
			resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
			resp.getWriter().write(
					"{\"status\": \"ERROR\", \"message\": \"Access Denied. You do not have the required permission.\"}");
			return;
		}

		chain.doFilter(request, response);
	}
}