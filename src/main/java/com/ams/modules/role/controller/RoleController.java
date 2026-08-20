package com.ams.modules.role.controller;

import java.io.IOException;

import com.ams.modules.role.dto.CreateRoleRequest;
import com.ams.modules.role.dto.RoleResponse;
import com.ams.modules.role.repository.impl.RoleRepositoryImpl;
import com.ams.modules.role.service.RoleService;
import com.ams.modules.role.service.impl.RoleServiceImpl;
import com.ams.modules.role.validator.RoleValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/roles")
public class RoleController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private RoleService roleService;

	@Override
	public void init() throws ServletException {
		this.roleService = new RoleServiceImpl(new RoleRepositoryImpl(), new RoleValidator());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try {
			CreateRoleRequest createRoleRequest = new CreateRoleRequest();
			createRoleRequest.setRoleName(req.getParameter("roleName"));
			createRoleRequest.setDescription(req.getParameter("description"));

			RoleResponse createdRole = roleService.createRole(createRoleRequest);

			resp.setStatus(HttpServletResponse.SC_CREATED);
			resp.getWriter().write("{\"status\": \"SUCCESS\", \"roleName\": \"" + createdRole.getRoleName() + "\"}");
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"" + e.getMessage() + "\"}");
		}
	}
}