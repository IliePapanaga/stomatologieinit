package com.cl.mdd.server.core.data.model.reporting;

import com.cl.mdd.server.core.data.model.reporting.commons.RunReport;

import javax.annotation.Nullable;
import java.time.LocalDate;

public class RunClientReport extends RunReport {

    private LocalDate activityDateFrom;

    private LocalDate activityDateTo;

    @Nullable
    public LocalDate getActivityDateFrom() {
        return activityDateFrom;
    }

    public RunClientReport setActivityDateFrom(LocalDate activityDateFrom) {
        this.activityDateFrom = activityDateFrom;
        return this;
    }

    @Nullable
    public LocalDate getActivityDateTo() {
        return activityDateTo;
    }

    public RunClientReport setActivityDateTo(LocalDate activityDateTo) {
        this.activityDateTo = activityDateTo;
        return this;
    }
}
