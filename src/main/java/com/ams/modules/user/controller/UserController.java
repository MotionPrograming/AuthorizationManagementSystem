package com.ams.modules.user.controller;

import java.io.IOException;

import com.ams.modules.user.dto.CreateUserRequest;
import com.ams.modules.user.dto.UserResponse;
import com.ams.modules.user.repository.impl.UserRepositoryImpl;
import com.ams.modules.user.service.UserService;
import com.ams.modules.user.service.impl.UserServiceImpl;
import com.ams.modules.user.validator.UserValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/users")
public class UserController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private UserService userService;

	@Override
	public void init() throws ServletException {
		this.userService = new UserServiceImpl(new UserRepositoryImpl(), new UserValidator());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try {
			CreateUserRequest createUserRequest = new CreateUserRequest();
			createUserRequest.setUsername(req.getParameter("username"));
			createUserRequest.setEmail(req.getParameter("email"));
			createUserRequest.setPassword(req.getParameter("password"));
			createUserRequest.setFullName(req.getParameter("fullName"));

			UserResponse createdUser = userService.createUser(createUserRequest);

			resp.setStatus(HttpServletResponse.SC_CREATED);
			resp.getWriter().write("{\"status\": \"SUCCESS\", \"username\": \"" + createdUser.getUsername() + "\"}");
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"" + e.getMessage() + "\"}");
		}
	}
}