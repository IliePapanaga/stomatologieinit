package com.cl.mdd.server.core.service.reporting.reports.archetype.payments;

import com.cl.mdd.server.core.service.reporting.ReportRequest;
import com.cl.mdd.server.core.service.reporting.ReportFormat;
import com.cl.mdd.server.core.service.reporting.reports.controls.DateFrom;
import com.cl.mdd.server.core.service.reporting.reports.controls.DateGroupBy;
import com.cl.mdd.server.core.service.reporting.reports.controls.DateTo;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;

public class PaymentsReportRequest implements ReportRequest {

    private String reportId;

    private LocalDate from;

    private LocalDate to;

    private ReportFormat format;

    private String groupBy;

    public PaymentsReportRequest(String reportId,
                                 @Nullable LocalDate from,
                                 @Nullable LocalDate to,
                                 @Nullable String groupBy,
                                 String format) {
        this.reportId = reportId;
        this.from = from;
        this.to = to;
        this.format = ReportFormat.valueOf(format);
        this.groupBy = groupBy;
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
        ImmutableList.Builder<Parameter> builder = ImmutableList.builder();

        if (from != null) {
            builder.add(DateFrom.of(from));
        }

        if (to != null) {
            builder.add(DateTo.of(to));
        }

        if (groupBy != null) {
            builder.add(DateGroupBy.of(groupBy));
        }

        return builder.build();
    }

}
