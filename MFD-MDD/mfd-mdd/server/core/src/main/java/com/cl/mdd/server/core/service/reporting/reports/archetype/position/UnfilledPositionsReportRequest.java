package com.cl.mdd.server.core.service.reporting.reports.archetype.position;

import com.cl.mdd.server.core.service.reporting.ReportFormat;
import com.cl.mdd.server.core.service.reporting.ReportRequest;
import com.cl.mdd.server.core.service.reporting.reports.controls.AbstractParameter;
import com.cl.mdd.server.core.service.reporting.reports.controls.DateFrom;
import com.cl.mdd.server.core.service.reporting.reports.controls.DateTo;
import com.cl.mdd.server.core.service.reporting.reports.controls.PositionType;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;

public class UnfilledPositionsReportRequest implements ReportRequest {

    private static final String REQUIRED_SUBCATEGORY = "REQUIRED_SUBCATEGORY";

    private static final String MIN_UNFILLED_DAYS = "MIN_UNFILLED_DAYS";

    private static final String MAX_UNFILLED_DAYS = "MAX_UNFILLED_DAYS";

    private static final String LATITUDE = "LATITUDE";

    private static final String LONGITUDE = "LONGITUDE";

    private static final String RADIUS = "RADIUS";

    private String reportId;

    private LocalDate from;

    private LocalDate to;

    private final String requiredSubcategory;

    private final Integer minUnfilledDays;

    private final Integer maxUnfilledDays;

    private String positionType;

    private final Double latitude;

    private final Double longitude;

    private final Double radius;

    private ReportFormat format;

    public UnfilledPositionsReportRequest(String reportId,
                                          @Nullable LocalDate from,
                                          @Nullable LocalDate to,
                                          @Nullable String requiredSubcategory,
                                          @Nullable Integer minUnfilledDays,
                                          @Nullable Integer maxUnfilledDays,
                                          @Nullable String positionType,
                                          @Nullable Double latitude,
                                          @Nullable Double longitude,
                                          @Nullable Double radius,
                                          String format) {
        this.reportId = reportId;
        this.from = from;
        this.to = to;
        this.requiredSubcategory = requiredSubcategory;
        this.minUnfilledDays = minUnfilledDays;
        this.maxUnfilledDays = maxUnfilledDays;
        this.positionType = positionType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
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

        if (positionType != null) {
            builder.add(PositionType.of(positionType));
        }

        if (requiredSubcategory != null) {
            builder.add(AbstractParameter.of(REQUIRED_SUBCATEGORY, requiredSubcategory));
        }

        if (minUnfilledDays != null) {
            builder.add(AbstractParameter.of(MIN_UNFILLED_DAYS, String.valueOf(minUnfilledDays)));
        }

        if (maxUnfilledDays != null) {
            builder.add(AbstractParameter.of(MAX_UNFILLED_DAYS, String.valueOf(maxUnfilledDays)));
        }

        if (latitude != null) {
            builder.add(AbstractParameter.of(LATITUDE, String.valueOf(latitude)));
        }

        if (longitude != null) {
            builder.add(AbstractParameter.of(LONGITUDE, String.valueOf(longitude)));
        }

        if (radius != null) {
            builder.add(AbstractParameter.of(RADIUS, String.valueOf(radius)));
        }

        return builder.build();
    }
}
