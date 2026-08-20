package com.ams.modules.report.mapper;

import com.ams.modules.report.dto.ReportSummaryResponse;
import com.ams.modules.report.entity.SystemReportSummary;

public class ReportMapper {

	public static ReportSummaryResponse toResponse(SystemReportSummary summary) {
		if (summary == null)
			return null;
		return new ReportSummaryResponse(summary.getTotalUsers(), summary.getTotalAccessRequests(),
				summary.getPendingRequests(), summary.getApprovedRequests(), summary.getRejectedRequests(),
				summary.getTotalAuditLogs());
	}
}