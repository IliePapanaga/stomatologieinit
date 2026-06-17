package com.cl.mdd.server.core.data.model.reporting;

import com.cl.mdd.server.core.data.model.reporting.commons.RunReport;

import javax.annotation.Nullable;
import java.time.LocalDate;

public class RunProfessionalReport extends RunReport {

    private LocalDate activityDateFrom;

    private LocalDate activityDateTo;

    @Nullable
    public LocalDate getActivityDateFrom() {
        return activityDateFrom;
    }

    public RunProfessionalReport setActivityDateFrom(LocalDate activityDateFrom) {
        this.activityDateFrom = activityDateFrom;
        return this;
    }

    @Nullable
    public LocalDate getActivityDateTo() {
        return activityDateTo;
    }

    public RunProfessionalReport setActivityDateTo(LocalDate activityDateTo) {
        this.activityDateTo = activityDateTo;
        return this;
    }
}
