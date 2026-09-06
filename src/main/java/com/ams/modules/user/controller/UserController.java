package com.ams.modules.user.controller;

import java.io.IOException;
import java.util.List;

import com.ams.common.util.JsonUtil;
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

@WebServlet("/api/v1/users/*")
public class UserController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private UserService userService;

	@Override
	public void init() throws ServletException {
		this.userService = new UserServiceImpl(new UserRepositoryImpl(), new UserValidator());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try {
			List<UserResponse> users = userService.getAllUsers();
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write(JsonUtil.toJson(users));
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"" + e.getMessage() + "\"}");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		String pathInfo = req.getPathInfo();

		try {
			// POST /api/v1/users/assign-role
			if (pathInfo != null && pathInfo.equals("/assign-role")) {
				Long userId = Long.parseLong(req.getParameter("userId"));
				Long roleId = Long.parseLong(req.getParameter("roleId"));

				userService.assignRoleToUser(userId, roleId);

				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter()
						.write("{\"status\": \"SUCCESS\", \"message\": \"Role assigned to user successfully.\"}");
				return;
			}

			// Base POST /api/v1/users (Create User)
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