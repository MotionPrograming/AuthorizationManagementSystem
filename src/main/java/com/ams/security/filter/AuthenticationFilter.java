package com.ams.security.filter;

import java.io.IOException;
import java.util.Optional;

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

@WebFilter("/api/v1/*")
public class AuthenticationFilter implements Filter {

	private final UserRepository userRepository;

	public AuthenticationFilter() {
		this.userRepository = new UserRepositoryImpl();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		String authHeader = req.getHeader("Authorization");

		try {
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = authHeader.substring(7);
				if (JwtTokenProvider.validateToken(token)) {
					String username = JwtTokenProvider.getUsernameFromToken(token);

					if (username != null) {
						Optional<User> userOpt = userRepository.findByUsername(username);
						userOpt.ifPresent(user -> SecurityContextHolder.setContext(UserMapper.toUserResponse(user)));
					}
				}
			}
			chain.doFilter(request, response);
		} finally {
			SecurityContextHolder.clearContext();
		}
	}
}