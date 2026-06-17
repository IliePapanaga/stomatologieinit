package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;
import com.cl.mdd.server.core.validation.constraint.WeekDay;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Objects;

@ExpressionConstraint(expression = "#this?.startTime?.isBefore(#this?.endTime)", message = "{job.posting.work.schedules.valid.time}")
public class WorkScheduleModel extends MDDModel {

    @WeekDay
    private String weekDay;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    public String getWeekDay() {
        return weekDay;
    }

    public WorkScheduleModel setWeekDay(String weekDay) {
        this.weekDay = weekDay;
        return this;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public WorkScheduleModel setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public WorkScheduleModel setEndTime(LocalTime endTime) {
        this.endTime = endTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkScheduleModel that = (WorkScheduleModel) o;
        return Objects.equals(weekDay, that.weekDay) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(weekDay, startTime, endTime);
    }
}
