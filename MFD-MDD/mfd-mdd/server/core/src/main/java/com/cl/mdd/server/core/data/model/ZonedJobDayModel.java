package com.cl.mdd.server.core.data.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

public class ZonedJobDayModel extends MDDModel {

    private LocalDate date;

    private ZonedDateTime startTime;

    private ZonedDateTime endTime;

    private boolean excluded;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
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
        ZonedJobDayModel that = (ZonedJobDayModel) o;
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
