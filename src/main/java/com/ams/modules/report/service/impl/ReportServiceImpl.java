package com.ams.modules.report.service.impl;

import com.ams.modules.report.dto.ReportFilterRequest;
import com.ams.modules.report.dto.ReportSummaryResponse;
import com.ams.modules.report.entity.SystemReportSummary;
import com.ams.modules.report.mapper.ReportMapper;
import com.ams.modules.report.repository.ReportRepository;
import com.ams.modules.report.service.ReportService;
import com.ams.modules.report.validator.ReportValidator;

public class ReportServiceImpl implements ReportService {

	private final ReportRepository repository;
	private final ReportValidator validator;

	public ReportServiceImpl(ReportRepository repository, ReportValidator validator) {
		this.repository = repository;
		this.validator = validator;
	}

	@Override
	public ReportSummaryResponse generateSummaryReport(ReportFilterRequest filter) {
		validator.validateFilter(filter);

		SystemReportSummary summary = repository.getSystemSummary(filter != null ? filter.getStartDate() : null,
				filter != null ? filter.getEndDate() : null);

		return ReportMapper.toResponse(summary);
	}
}