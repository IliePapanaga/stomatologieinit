package com.cl.mdd.server.core.data.model.reporting;

import com.cl.mdd.server.core.data.model.reporting.commons.PositionType;
import com.cl.mdd.server.core.data.model.reporting.commons.RunReport;

import javax.annotation.Nullable;
import java.time.LocalDate;

public class RunPositionsReport extends RunReport {

    private LocalDate from;

    private LocalDate to;

    private PositionType positionType;

    @Nullable
    public LocalDate getFrom() {
        return from;
    }

    public RunPositionsReport setFrom(LocalDate from) {
        this.from = from;
        return this;
    }

    @Nullable
    public LocalDate getTo() {
        return to;
    }

    public RunPositionsReport setTo(LocalDate to) {
        this.to = to;
        return this;
    }

    @Nullable
    public PositionType getPositionType() {
        return positionType;
    }

    public RunPositionsReport setPositionType(PositionType positionType) {
        this.positionType = positionType;
        return this;
    }
}
