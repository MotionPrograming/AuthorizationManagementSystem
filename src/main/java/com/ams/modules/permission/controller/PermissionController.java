package com.ams.modules.permission.controller;

import java.io.IOException;

import com.ams.modules.permission.dto.CreatePermissionRequest;
import com.ams.modules.permission.dto.PermissionResponse;
import com.ams.modules.permission.repository.impl.PermissionRepositoryImpl;
import com.ams.modules.permission.service.PermissionService;
import com.ams.modules.permission.service.impl.PermissionServiceImpl;
import com.ams.modules.permission.validator.PermissionValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/permissions")
public class PermissionController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private PermissionService permissionService;

	@Override
	public void init() throws ServletException {
		this.permissionService = new PermissionServiceImpl(new PermissionRepositoryImpl(), new PermissionValidator());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try {
			CreatePermissionRequest createPermissionRequest = new CreatePermissionRequest();
			createPermissionRequest.setPermissionName(req.getParameter("permissionName"));
			createPermissionRequest.setDescription(req.getParameter("description"));

			PermissionResponse createdPermission = permissionService.createPermission(createPermissionRequest);

			resp.setStatus(HttpServletResponse.SC_CREATED);
			resp.getWriter().write(
					"{\"status\": \"SUCCESS\", \"permissionName\": \"" + createdPermission.getPermissionName() + "\"}");
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"" + e.getMessage() + "\"}");
		}
	}
}