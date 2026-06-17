package com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview;

import com.cl.mdd.server.core.data.persistent.listeners.AbsoluteStartTimeAdjuster;
import com.cl.mdd.server.core.data.persistent.model.common.HasAbsoluteStartTime;
import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "JOB_INTERVIEW_OPTIONS")
@EntityListeners(AbsoluteStartTimeAdjuster.class)
public class JobInterviewOption extends Identifiable implements HasAbsoluteStartTime {

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_job_interview_id")
    private JobInterview jobInterview;

    @Column(name = "zoned_start_date_time", nullable = false)
    private ZonedDateTime zonedStartDateTime;

    public LocalDate getDate() {
        return date;
    }

    public JobInterviewOption setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public LocalTime getTime() {
        return time;
    }

    public JobInterviewOption setTime(LocalTime time) {
        this.time = time;
        return this;
    }

    public JobInterview getJobInterview() {
        return jobInterview;
    }

    public JobInterviewOption setJobInterview(JobInterview jobInterview) {
        this.jobInterview = jobInterview;
        return this;
    }

    @Override
    public LocalDate getStartDate() {
        return date;
    }

    @Override
    public LocalTime getStartTime() {
        return time;
    }

    @Override
    public ZoneId getTimeZone() {
        return jobInterview.getApplication().getJobPosting().getLocation().getTimeZone();
    }

    @Override
    public void setZonedStartDateTime(ZonedDateTime zonedStartDateTime) {
        this.zonedStartDateTime = zonedStartDateTime;
    }

    @Override
    public ZonedDateTime getZonedStartDateTime() {
        return zonedStartDateTime;
    }
}
