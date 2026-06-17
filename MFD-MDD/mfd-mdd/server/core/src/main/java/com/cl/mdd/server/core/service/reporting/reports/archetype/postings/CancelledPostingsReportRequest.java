package com.cl.mdd.server.core.service.reporting.reports.archetype.postings;

import com.cl.mdd.server.core.service.reporting.ReportFormat;
import com.cl.mdd.server.core.service.reporting.ReportRequest;
import com.cl.mdd.server.core.service.reporting.reports.controls.DateFrom;
import com.cl.mdd.server.core.service.reporting.reports.controls.DateTo;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;

public class CancelledPostingsReportRequest implements ReportRequest {

    private String reportId;

    private LocalDate from;

    private LocalDate to;

    private ReportFormat format;


    public CancelledPostingsReportRequest(String reportId,
                                          @Nullable LocalDate from,
                                          @Nullable LocalDate to,
                                          String format) {
        this.reportId = reportId;
        this.from = from;
        this.to = to;
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
        ImmutableList.Builder<Parameter> builder = ImmutableList.builder();

        if (from != null) {
            builder.add(DateFrom.of(from));
        }

        if (to != null) {
            builder.add(DateTo.of(to));
        }
        return builder.build();

    }

}
