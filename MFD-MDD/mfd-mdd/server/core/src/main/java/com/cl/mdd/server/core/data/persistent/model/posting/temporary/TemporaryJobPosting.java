package com.cl.mdd.server.core.data.persistent.model.posting.temporary;

import com.cl.mdd.server.core.data.persistent.listeners.AbsoluteEndTimeAdjuster;
import com.cl.mdd.server.core.data.persistent.model.common.HasAbsoluteEndTime;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Entity
@DiscriminatorValue(TemporaryJobPosting.DISCRIMINATOR)
@EntityListeners(AbsoluteEndTimeAdjuster.class)
public class TemporaryJobPosting extends JobPosting implements HasAbsoluteEndTime {

    public static final String JOB_DAY_STRATEGY_SIMPLE = "SIMPLE";

    public static final String JOB_DAY_STRATEGY_WEEKLY = "WEEKLY";

    public static final String JOB_DAY_STRATEGY_COMPLEX = "COMPLEX";

    public static final String DISCRIMINATOR = "TEMPORARY_JOB_POSTING";

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "job_day_strategy")
    private String jobDayStrategy;

    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<JobDay> jobDays = newHashSet();

    @OneToMany(mappedBy = "temporaryJobPosting")
    private Set<TemporaryJobPostingApplication> applications = newHashSet();

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "zoned_end_date_time")
    private ZonedDateTime zonedEndDateTime;

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getEndTime() {
        return endTime == null ? LocalTime.MAX : endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getJobDayStrategy() {
        return jobDayStrategy;
    }

    public void setJobDayStrategy(String jobDayStrategy) {
        this.jobDayStrategy = jobDayStrategy;
    }

    public Set<JobDay> getJobDays() {
        return jobDays;
    }

    public void setJobDays(Set<JobDay> jobDays) {
        this.jobDays = jobDays;
    }

    public Set<TemporaryJobPostingApplication> getApplications() {
        return applications;
    }

    public TemporaryJobPosting setApplications(Set<TemporaryJobPostingApplication> applications) {
        this.applications = applications;
        return this;
    }

    public LocalTime getStartTime() {
        return startTime == null ? LocalTime.MIN : startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public ZonedDateTime getZonedEndDateTime() {
        return zonedEndDateTime;
    }

    @Override
    public void setZonedEndDateTime(ZonedDateTime zonedEndDateTime) {
        this.zonedEndDateTime = zonedEndDateTime;
    }
}
