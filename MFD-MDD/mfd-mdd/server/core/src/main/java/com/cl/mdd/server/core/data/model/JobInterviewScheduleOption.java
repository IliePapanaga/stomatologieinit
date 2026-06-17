package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class JobInterviewScheduleOption extends MDDModel {

    @NotNull(message = "{job.interview.option.date.not.null}")
    @ExpressionConstraint(expression = "#this == null or T(java.time.LocalDate).now().isBefore(#this)", message = "{job.interview.option.date.future}")
    private LocalDate date;

    @NotNull(message = "{job.interview.option.time.not.null}")
    private LocalTime time;

    public LocalDate getDate() {
        return date;
    }

    public JobInterviewScheduleOption setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public LocalTime getTime() {
        return time;
    }

    public JobInterviewScheduleOption setTime(LocalTime time) {
        this.time = time;
        return this;
    }
}
