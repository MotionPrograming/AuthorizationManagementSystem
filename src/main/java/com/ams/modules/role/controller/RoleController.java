package com.ams.modules.role.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.ams.common.util.JsonUtil;
import com.ams.modules.role.dto.CreateRoleRequest;
import com.ams.modules.role.dto.UpdateRoleRequest;
import com.ams.modules.role.repository.impl.RoleRepositoryImpl;
import com.ams.modules.role.service.RoleService;
import com.ams.modules.role.service.impl.RoleServiceImpl;
import com.ams.modules.role.validator.RoleValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/roles/*")
public class RoleController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private RoleService service;

	@Override
	public void init() throws ServletException {
		service = new RoleServiceImpl(new RoleRepositoryImpl(), new RoleValidator());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		try {
			String path = req.getPathInfo();

			if (path == null || "/".equals(path)) {
				write(resp, service.getAllRoles(), 200);
				return;
			}

			Long roleId = Long.valueOf(path.substring(1));
			write(resp, service.getRoleById(roleId), 200);

		} catch (Exception e) {
			error(resp, e, 404);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		try {
			if ("/assign-permission".equals(req.getPathInfo())) {

				Long roleId = Long.valueOf(req.getParameter("roleId"));

				Long permissionId = Long.valueOf(req.getParameter("permissionId"));

				service.assignPermissionToRole(roleId, permissionId);

				write(resp, JsonUtil.response("SUCCESS", "Permission assigned successfully"), 200);

				return;
			}

			CreateRoleRequest request = new CreateRoleRequest();

			request.setRoleName(req.getParameter("roleName"));
			request.setDescription(req.getParameter("description"));

			write(resp, service.createRole(request), 201);

		} catch (Exception e) {
			error(resp, e, 400);
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		try {
			Long roleId = requiredId(req);

			UpdateRoleRequest request;

			String contentType = req.getContentType();

			if (contentType != null && contentType.toLowerCase().startsWith("application/json")) {

				// JSON request body
				String body = readRequestBody(req);

				request = JsonUtil.fromJson(body, UpdateRoleRequest.class);

			} else {

				// application/x-www-form-urlencoded
				String body = readRequestBody(req);

				Map<String, String> params = parseFormBody(body);

				request = new UpdateRoleRequest();

				request.setRoleName(params.get("roleName"));

				request.setDescription(params.get("description"));
			}

			if (request == null) {
				error(resp, new IllegalArgumentException("Request body is required"), 400);
				return;
			}

			if (request.getRoleName() == null || request.getRoleName().trim().isEmpty()) {

				error(resp, new IllegalArgumentException("Role name is required."), 400);
				return;
			}

			if (!service.updateRole(roleId, request)) {
				error(resp, new RuntimeException("Role not found"), 404);
				return;
			}

			write(resp, JsonUtil.response("SUCCESS", "Role updated successfully"), 200);

		} catch (NumberFormatException e) {

			error(resp, new IllegalArgumentException("Invalid role id"), 400);

		} catch (Exception e) {
			error(resp, e, 400);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		try {

			if (!service.deleteRole(requiredId(req))) {

				error(resp, new RuntimeException("Role not found"), 404);

				return;
			}

			write(resp, JsonUtil.response("SUCCESS", "Role deleted successfully"), 200);

		} catch (Exception e) {
			error(resp, e, 400);
		}
	}

	private Long requiredId(HttpServletRequest req) {

		String path = req.getPathInfo();

		try {
			if (path == null || path.length() <= 1) {

				throw new IllegalArgumentException("Role id is required");
			}

			return Long.valueOf(path.substring(1));

		} catch (NumberFormatException e) {

			throw new IllegalArgumentException("Invalid role id");
		}
	}

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

	private Map<String, String> parseFormBody(String body) {

		Map<String, String> params = new HashMap<>();

		if (body == null || body.isBlank()) {
			return params;
		}

		String[] pairs = body.split("&");

		for (String pair : pairs) {

			String[] keyValue = pair.split("=", 2);

			String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);

			String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : "";

			params.put(key, value);
		}

		return params;
	}

	private void write(HttpServletResponse resp, Object object, int status) throws IOException {

		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.setStatus(status);

		resp.getWriter().write(JsonUtil.toJson(object));
	}

	private void error(HttpServletResponse resp, Exception e, int status) throws IOException {

		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.setStatus(status);

		resp.getWriter().write(JsonUtil.response("ERROR", e.getMessage() == null ? "Request failed" : e.getMessage()));
	}
}