package com.cl.mdd.server.core.data.persistent.model.posting.temporary;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.common.WeekDay;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "WORK_SCHEDULES", uniqueConstraints = @UniqueConstraint(columnNames = {"fk_job_posting_id", "fk_week_day_id"}))
public class WorkSchedule extends AuditedEntity {

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "fk_week_day_id", nullable = false)
    private WeekDay weekDay;

    public LocalTime getStartTime() {
        return startTime;
    }

    public WorkSchedule setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public WorkSchedule setEndTime(LocalTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public WeekDay getWeekDay() {
        return weekDay;
    }

    public WorkSchedule setWeekDay(WeekDay weekDay) {
        this.weekDay = weekDay;
        return this;
    }
}
