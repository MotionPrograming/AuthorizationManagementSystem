package com.ams.modules.auth.controller;

import java.io.IOException;

import com.ams.modules.auth.dto.LoginRequest;
import com.ams.modules.auth.dto.LoginResponse;
import com.ams.modules.auth.repository.impl.AuthRepositoryImpl;
import com.ams.modules.auth.service.AuthService;
import com.ams.modules.auth.service.impl.AuthServiceImpl;
import com.ams.modules.auth.validator.AuthValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/auth/login")
public class AuthController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private AuthService authService;

	@Override
	public void init() throws ServletException {

		this.authService = new AuthServiceImpl(new AuthRepositoryImpl(), null, new AuthValidator());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try {
			String username = req.getParameter("username");
			String password = req.getParameter("password");

			LoginRequest loginRequest = new LoginRequest();
			loginRequest.setUsername(username);
			loginRequest.setPassword(password);

			LoginResponse response = authService.login(loginRequest);

			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write("{\"status\": \"SUCCESS\", \"token\": \"" + response.getToken() + "\"}");
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"" + e.getMessage() + "\"}");
		}
	}
}