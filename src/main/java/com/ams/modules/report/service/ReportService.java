package com.ams.modules.report.service;

import com.ams.modules.report.dto.ReportFilterRequest;
import com.ams.modules.report.dto.ReportSummaryResponse;

public interface ReportService {
	ReportSummaryResponse generateSummaryReport(ReportFilterRequest filter);
}