package com.cl.mdd.server.core.service.reporting.reports.archetype.cc;

import com.cl.mdd.server.core.service.reporting.ReportRequest;
import com.cl.mdd.server.core.service.reporting.ReportFormat;

import java.util.Collections;
import java.util.List;

public class ExpiredCreditCardReportRequest implements ReportRequest {

    private String reportId;

    private ReportFormat format;

    public ExpiredCreditCardReportRequest(String reportId, String format) {
        this.reportId = reportId;
        this.format = ReportFormat.valueOf(format);
    }

    @Override
    public String getReportId() {
        return reportId;
    }

    @Override
    public ReportFormat getFormat() {
        return format;
    }

    @Override
    public List<Parameter> parameters() {
        return Collections.emptyList();
    }
}
