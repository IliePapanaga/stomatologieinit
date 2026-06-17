package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;


@ExpressionConstraint(expression = "#this.startTime == null or #this.endTime == null or #this.startTime.isBefore(#this.endTime)",
        message = "{job.day.start.times.valid}")
public class JobDayModel extends MDDModel {

    @NotNull(message = "{job.day.date.not.null}")
    private LocalDate date;

    @NotNull(message = "{job.day.start.time.not.null}")
    private LocalTime startTime;

    @NotNull(message = "{job.day.end.time.not.null}")
    private LocalTime endTime;

    private boolean excluded;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public void setExcluded(boolean excluded) {
        this.excluded = excluded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobDayModel that = (JobDayModel) o;
        return excluded == that.excluded &&
                Objects.equals(date, that.date) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, startTime, endTime, excluded);
    }
}
