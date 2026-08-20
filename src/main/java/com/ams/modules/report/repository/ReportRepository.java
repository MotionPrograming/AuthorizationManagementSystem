package com.ams.modules.report.repository;

import java.time.LocalDate;

import com.ams.modules.report.entity.SystemReportSummary;

public interface ReportRepository {
	SystemReportSummary getSystemSummary(LocalDate startDate, LocalDate endDate);
}