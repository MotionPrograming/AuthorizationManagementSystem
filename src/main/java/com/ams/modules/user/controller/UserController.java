package com.ams.modules.user.controller;

import java.io.IOException;

import com.ams.common.util.JsonUtil;
import com.ams.modules.role.repository.impl.RoleRepositoryImpl;
import com.ams.modules.user.dto.CreateUserRequest;
import com.ams.modules.user.dto.UpdateUserRequest;
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
	private UserService service;

	@Override
	public void init() throws ServletException {
		service = new UserServiceImpl(new UserRepositoryImpl(), new UserValidator(), new RoleRepositoryImpl());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			Long id = id(req);
			write(resp, id == null ? service.getAllUsers() : service.getUserById(id), 200);
		} catch (Exception e) {
			error(resp, e, 404);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			if ("/assign-role".equals(req.getPathInfo())) {
				service.assignRoleToUser(requiredLong(req, "userId"), requiredLong(req, "roleId"));
				write(resp, JsonUtil.response("SUCCESS", "Role assigned successfully"), 200);
				return;
			}
			CreateUserRequest r = new CreateUserRequest();
			r.setUsername(req.getParameter("username"));
			r.setEmail(req.getParameter("email"));
			r.setPassword(req.getParameter("password"));
			r.setFullName(req.getParameter("fullName"));
			write(resp, service.createUser(r), 201);
		} catch (Exception e) {
			error(resp, e, 400);
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			Long id = requiredLong(req, "id");
			UpdateUserRequest r = new UpdateUserRequest();
			r.setEmail(req.getParameter("email"));
			r.setFullName(req.getParameter("fullName"));
			r.setStatus(req.getParameter("status"));
			if (!service.updateUser(id, r)) {
				error(resp, new RuntimeException("User not found"), 404);
				return;
			}
			write(resp, JsonUtil.response("SUCCESS", "User updated successfully"), 200);
		} catch (Exception e) {
			error(resp, e, 400);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			if (!service.deleteUser(requiredLong(req, "id"))) {
				error(resp, new RuntimeException("User not found"), 404);
				return;
			}
			write(resp, JsonUtil.response("SUCCESS", "User deleted successfully"), 200);
		} catch (Exception e) {
			error(resp, e, 400);
		}
	}

	private Long id(HttpServletRequest req) {
		String v = req.getPathInfo();
		if (v == null || v.length() <= 1)
			return null;
		try {
			return Long.valueOf(v.substring(1));
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid user id");
		}
	}

	private Long requiredLong(HttpServletRequest req, String name) {
		String v = req.getParameter(name);
		if (v == null && "id".equals(name))
			v = req.getPathInfo() == null ? null : req.getPathInfo().substring(1);
		try {
			return Long.valueOf(v);
		} catch (Exception e) {
			throw new IllegalArgumentException(name + " is required");
		}
	}

	private void write(HttpServletResponse r, Object o, int status) throws IOException {
		r.setContentType("application/json");
		r.setCharacterEncoding("UTF-8");
		r.setStatus(status);
		r.getWriter().write(JsonUtil.toJson(o));
	}

	private void error(HttpServletResponse r, Exception e, int status) throws IOException {
		r.setContentType("application/json");
		r.setCharacterEncoding("UTF-8");
		r.setStatus(status);
		r.getWriter().write(JsonUtil.response("ERROR", e.getMessage() == null ? "Request failed" : e.getMessage()));
	}
}
