package com.cl.mdd.server.core.data.persistent.model.posting.temporary;

import com.cl.mdd.server.core.data.persistent.listeners.AbsoluteEndTimeAdjuster;
import com.cl.mdd.server.core.data.persistent.listeners.AbsoluteStartTimeAdjuster;
import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.common.HasAbsoluteEndTime;
import com.cl.mdd.server.core.data.persistent.model.common.HasAbsoluteStartTime;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

import static com.google.api.client.util.Sets.newHashSet;

@Entity
@Table(name = "JOB_DAYS", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
              JobDay.DATE  , JobDay.FK_POSTING_ID
        })
})
@EntityListeners(value = {AbsoluteStartTimeAdjuster.class, AbsoluteEndTimeAdjuster.class})
public class JobDay extends AuditedEntity implements HasAbsoluteStartTime, HasAbsoluteEndTime {

    public static final String CHECKED_IN = "CHECKED_IN";

    public static final String NEW = "NEW";

    public static final String ACCEPTED = "ACCEPTED";

    /**
     * transient status
     */
    public static final String NEED_CHECK_IN = "NEED_CHECK_IN";

    /**
     * transient status
     */
    public static final String SOS = "SOS";

    /**
     * transient status
     */
    public static final String NO_SHOW = "NO_SHOW";

    /**
     * transient status
     */
    public static final String REJECTED = "REJECTED";

    static final String FK_POSTING_ID = "fk_posting_id";

    static final String DATE = "date";

    @Column(name = DATE, nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "excluded", nullable = false)
    private boolean excluded;

    @Column(name = "sos_requested", nullable = false)
    private boolean sosRequested;

    @Column(name = "alerted", nullable = false)
    private boolean alerted;

    @Column(name = "status", nullable = false)
    private String status = NEW;

    @Column(name = "notified_start_soon", nullable = false)
    private boolean notifiedAboutStartSoon;

    @Column(name = "notified_started", nullable = false)
    private boolean notifiedAboutStarted;

    @Column(name = "zoned_start_date_time", nullable = false)
    private ZonedDateTime zonedStartDateTime;

    @Column(name = "zoned_end_date_time", nullable = false)
    private ZonedDateTime zonedEndDateTime;

    @ManyToOne
    @JoinColumn(name = FK_POSTING_ID, nullable = false)
    private JobPosting jobPosting;

    @ManyToMany(mappedBy = "jobDays")
    private Set<TemporaryJobPostingApplication> applications = newHashSet();

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public boolean isAlerted() {
        return alerted;
    }

    public JobDay setAlerted(boolean alerted) {
        this.alerted = alerted;
        return this;
    }

    public boolean isSosRequested() {
        return sosRequested;
    }

    public JobDay setSosRequested(boolean sosRequested) {
        this.sosRequested = sosRequested;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isNotifiedAboutStartSoon() {
        return notifiedAboutStartSoon;
    }

    public JobDay setNotifiedAboutStartSoon(boolean notifiedAboutStartSoon) {
        this.notifiedAboutStartSoon = notifiedAboutStartSoon;
        return this;
    }

    public boolean isNotifiedAboutStarted() {
        return notifiedAboutStarted;
    }

    public JobDay setNotifiedAboutStarted(boolean notifiedAboutStarted) {
        this.notifiedAboutStarted = notifiedAboutStarted;
        return this;
    }

    public JobPosting getJobPosting() {
        return jobPosting;
    }

    public JobDay setJobPosting(JobPosting jobPosting) {
        this.jobPosting = jobPosting;
        return this;
    }

    public Set<TemporaryJobPostingApplication> getApplications() {
        return applications;
    }

    public JobDay setApplications(Set<TemporaryJobPostingApplication> applications) {
        this.applications = applications;
        return this;
    }

    @Override
    public LocalDate getStartDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    @Override
    public ZoneId getTimeZone() {
        return this.getJobPosting().getLocation().getTimeZone();
    }

    @Override
    public void setZonedStartDateTime(ZonedDateTime zonedStartDateTime) {
        this.zonedStartDateTime = zonedStartDateTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public LocalDate getEndDate() {
        return date;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public void setZonedEndDateTime(ZonedDateTime zonedEndDateTime) {
        this.zonedEndDateTime = zonedEndDateTime;
    }

    @Override
    public ZonedDateTime getZonedStartDateTime() {
        return zonedStartDateTime;
    }

    @Override
    public ZonedDateTime getZonedEndDateTime() {
        return zonedEndDateTime;
    }
}
