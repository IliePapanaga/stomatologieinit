package com.cl.mdd.server.core.data.model.reporting;

import com.cl.mdd.server.core.data.model.reporting.commons.RunReport;

import javax.annotation.Nullable;
import java.time.LocalDate;

public class RunCancelledPostingsReport extends RunReport {

    private LocalDate from;

    private LocalDate to;

    @Nullable
    public LocalDate getFrom() {
        return from;
    }

    public RunCancelledPostingsReport setFrom(LocalDate from) {
        this.from = from;
        return this;
    }

    @Nullable
    public LocalDate getTo() {
        return to;
    }

    public RunCancelledPostingsReport setTo(LocalDate to) {
        this.to = to;
        return this;
    }

}
