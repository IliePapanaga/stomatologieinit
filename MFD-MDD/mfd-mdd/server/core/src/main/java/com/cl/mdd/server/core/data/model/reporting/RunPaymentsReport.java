package com.cl.mdd.server.core.data.model.reporting;

import com.cl.mdd.server.core.data.model.reporting.commons.GroupByDate;
import com.cl.mdd.server.core.data.model.reporting.commons.RunReport;

import javax.annotation.Nullable;
import java.time.LocalDate;

public class RunPaymentsReport extends RunReport {

    private LocalDate from;

    private LocalDate to;

    private GroupByDate groupBy;

    @Nullable
    public LocalDate getFrom() {
        return from;
    }

    public RunPaymentsReport setFrom(LocalDate from) {
        this.from = from;
        return this;
    }

    @Nullable
    public LocalDate getTo() {
        return to;
    }

    public RunPaymentsReport setTo(LocalDate to) {
        this.to = to;
        return this;
    }

    @Nullable
    public GroupByDate getGroupBy() {
        return groupBy;
    }

    public RunPaymentsReport setGroupBy(GroupByDate groupBy) {
        this.groupBy = groupBy;
        return this;
    }
}
