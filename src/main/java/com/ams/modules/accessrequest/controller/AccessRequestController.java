package com.ams.modules.accessrequest.controller;

import java.io.IOException;

import com.ams.modules.accessrequest.dto.AccessRequestRequest;
import com.ams.modules.accessrequest.dto.AccessRequestResponse;
import com.ams.modules.accessrequest.repository.impl.AccessRequestRepositoryImpl;
import com.ams.modules.accessrequest.service.AccessRequestService;
import com.ams.modules.accessrequest.service.impl.AccessRequestServiceImpl;
import com.ams.modules.accessrequest.validator.AccessRequestValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/access-requests")
public class AccessRequestController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private AccessRequestService service;

	@Override
	public void init() throws ServletException {
		this.service = new AccessRequestServiceImpl(new AccessRequestRepositoryImpl(), new AccessRequestValidator());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try {
			AccessRequestRequest requestDto = new AccessRequestRequest();

			String userIdParam = req.getParameter("userId");
			if (userIdParam != null && !userIdParam.trim().isEmpty()) {
				requestDto.setUserId(Long.parseLong(userIdParam));
			}

			requestDto.setRequestType(req.getParameter("requestType"));
			requestDto.setRequestReason(req.getParameter("requestReason"));

			AccessRequestResponse created = service.createRequest(requestDto);

			resp.setStatus(HttpServletResponse.SC_CREATED);
			resp.getWriter().write("{\"status\": \"SUCCESS\", \"requestId\": " + created.getRequestId() + "}");
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"" + e.getMessage() + "\"}");
		}
	}
}