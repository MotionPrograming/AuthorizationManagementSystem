package com.ams.modules.report.controller;

import java.io.IOException;

import com.ams.modules.report.dto.ReportSummaryResponse;
import com.ams.modules.report.repository.impl.ReportRepositoryImpl;
import com.ams.modules.report.service.ReportService;
import com.ams.modules.report.service.impl.ReportServiceImpl;
import com.ams.modules.report.validator.ReportValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/reports/summary")
public class ReportController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private ReportService reportService;

	@Override
	public void init() throws ServletException {
		this.reportService = new ReportServiceImpl(new ReportRepositoryImpl(), new ReportValidator());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try {
			ReportSummaryResponse response = reportService.generateSummaryReport(null);

			String json = String.format(
					"{\"totalUsers\": %d, \"totalAccessRequests\": %d, \"pendingRequests\": %d, \"approvedRequests\": %d, \"rejectedRequests\": %d, \"totalAuditLogs\": %d}",
					response.getTotalUsers(), response.getTotalAccessRequests(), response.getPendingRequests(),
					response.getApprovedRequests(), response.getRejectedRequests(), response.getTotalAuditLogs());

			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write(json);
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().write("{\"status\": \"ERROR\", \"message\": \"" + e.getMessage() + "\"}");
		}
	}
}