package com.ams.security.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class SecurityHeadersFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletResponse resp = (HttpServletResponse) response;

		resp.setHeader("X-Content-Type-Options", "nosniff");
		resp.setHeader("X-Frame-Options", "DENY");
		resp.setHeader("X-XSS-Protection", "1; mode=block");
		resp.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
		resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

		chain.doFilter(request, response);
	}
}