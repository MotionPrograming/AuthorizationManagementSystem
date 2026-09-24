
package com.ams.modules.user.controller;

import java.io.BufferedReader;
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

			CreateUserRequest request = readCreateUserRequest(req);

			write(resp, service.createUser(request), 201);

		} catch (Exception e) {
			error(resp, e, 400);
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		try {

			Long id = requiredLong(req, "id");

			UpdateUserRequest request = readUpdateUserRequest(req);

			if (!service.updateUser(id, request)) {
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

	/**
	 * Reads CreateUserRequest from JSON or traditional request parameters.
	 */
	private CreateUserRequest readCreateUserRequest(HttpServletRequest req) throws IOException {

		if (isJsonRequest(req)) {
			return JsonUtil.fromJson(readRequestBody(req), CreateUserRequest.class);
		}

		CreateUserRequest request = new CreateUserRequest();

		request.setUsername(req.getParameter("username"));
		request.setEmail(req.getParameter("email"));
		request.setPassword(req.getParameter("password"));
		request.setFullName(req.getParameter("fullName"));

		return request;
	}

	/**
	 * Reads UpdateUserRequest from JSON or traditional request parameters.
	 */
	private UpdateUserRequest readUpdateUserRequest(HttpServletRequest req) throws IOException {

		if (isJsonRequest(req)) {
			return JsonUtil.fromJson(readRequestBody(req), UpdateUserRequest.class);
		}

		UpdateUserRequest request = new UpdateUserRequest();

		request.setEmail(req.getParameter("email"));
		request.setFullName(req.getParameter("fullName"));
		request.setStatus(req.getParameter("status"));

		return request;
	}

	/**
	 * Checks whether the request body contains JSON.
	 */
	private boolean isJsonRequest(HttpServletRequest req) {
		String contentType = req.getContentType();

		return contentType != null && contentType.toLowerCase().startsWith("application/json");
	}

	/**
	 * Reads the complete HTTP request body.
	 */
	private String readRequestBody(HttpServletRequest req) throws IOException {

		StringBuilder body = new StringBuilder();

		try (BufferedReader reader = req.getReader()) {
			String line;

			while ((line = reader.readLine()) != null) {
				body.append(line);
			}
		}

		return body.toString();
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

		if (v == null && "id".equals(name)) {
			String path = req.getPathInfo();

			v = path == null ? null : path.substring(1);
		}

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
