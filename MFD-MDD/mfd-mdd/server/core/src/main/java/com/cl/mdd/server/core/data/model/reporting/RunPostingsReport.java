package com.cl.mdd.server.core.data.model.reporting;

import com.cl.mdd.server.core.data.model.reporting.commons.GroupByDate;
import com.cl.mdd.server.core.data.model.reporting.commons.RunReport;

import javax.annotation.Nullable;
import java.time.LocalDate;

public class RunPostingsReport extends RunReport {

    private LocalDate from;

    private LocalDate to;

    private GroupByDate groupBy;

    @Nullable
    public LocalDate getFrom() {
        return from;
    }

    public RunPostingsReport setFrom(LocalDate from) {
        this.from = from;
        return this;
    }

    @Nullable
    public LocalDate getTo() {
        return to;
    }

    public RunPostingsReport setTo(LocalDate to) {
        this.to = to;
        return this;
    }

    @Nullable
    public GroupByDate getGroupBy() {
        return groupBy;
    }

    public RunPostingsReport setGroupBy(GroupByDate groupBy) {
        this.groupBy = groupBy;
        return this;
    }
}
