package com.ams.modules.audit.controller;

import java.io.IOException;

import com.ams.modules.audit.dto.AuditLogResponse;
import com.ams.modules.audit.dto.CreateAuditLogRequest;
import com.ams.modules.audit.repository.impl.AuditRepositoryImpl;
import com.ams.modules.audit.service.AuditService;
import com.ams.modules.audit.service.impl.AuditServiceImpl;
import com.ams.modules.audit.validator.AuditValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/audits")
public class AuditController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private AuditService auditService;

	@Override
	public void init() throws ServletException {
		this.auditService = new AuditServiceImpl(new AuditRepositoryImpl(), new AuditValidator());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try {
			CreateAuditLogRequest createRequest = new CreateAuditLogRequest();

			String userIdParam = req.getParameter("userId");
			if (userIdParam != null && !userIdParam.trim().isEmpty()) {
				createRequest.setUserId(Long.parseLong(userIdParam));
			}

			createRequest.setAction(req.getParameter("action"));
			createRequest.setDescription(req.getParameter("description"));
			createRequest.setIpAddress(req.getRemoteAddr());

			AuditLogResponse response = auditService.logAction(createRequest);

			resp.setStatus(HttpServletResponse.SC_CREATED);
			resp.getWriter().write("{\"status\": \"SUCCESS\", \"action\": \"" + response.getAction() + "\"}");
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"" + e.getMessage() + "\"}");
		}
	}
}