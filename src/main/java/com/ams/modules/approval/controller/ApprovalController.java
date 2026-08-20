package com.ams.modules.approval.controller;

import java.io.IOException;

import com.ams.modules.approval.dto.ApprovalResponse;
import com.ams.modules.approval.dto.CreateApprovalRequest;
import com.ams.modules.approval.repository.impl.ApprovalRepositoryImpl;
import com.ams.modules.approval.service.ApprovalService;
import com.ams.modules.approval.service.impl.ApprovalServiceImpl;
import com.ams.modules.approval.validator.ApprovalValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/approvals")
public class ApprovalController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private ApprovalService approvalService;

	@Override
	public void init() throws ServletException {
		this.approvalService = new ApprovalServiceImpl(new ApprovalRepositoryImpl(), new ApprovalValidator());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try {
			CreateApprovalRequest createRequest = new CreateApprovalRequest();

			String requestIdParam = req.getParameter("requestId");
			if (requestIdParam != null && !requestIdParam.trim().isEmpty()) {
				createRequest.setRequestId(Long.parseLong(requestIdParam));
			}

			String approverIdParam = req.getParameter("approverId");
			if (approverIdParam != null && !approverIdParam.trim().isEmpty()) {
				createRequest.setApproverId(Long.parseLong(approverIdParam));
			}

			createRequest.setDecision(req.getParameter("decision"));
			createRequest.setComments(req.getParameter("comments"));

			ApprovalResponse response = approvalService.processApproval(createRequest);

			resp.setStatus(HttpServletResponse.SC_CREATED);
			resp.getWriter().write("{\"status\": \"SUCCESS\", \"decision\": \"" + response.getDecision() + "\"}");
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"" + e.getMessage() + "\"}");
		}
	}
}