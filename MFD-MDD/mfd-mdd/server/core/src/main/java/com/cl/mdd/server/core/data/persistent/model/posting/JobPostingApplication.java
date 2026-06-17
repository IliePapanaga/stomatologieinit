package com.cl.mdd.server.core.data.persistent.model.posting;

import com.cl.mdd.server.core.data.persistent.listeners.AbsoluteStartTimeAdjuster;
import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.common.HasAbsoluteStartTime;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.of;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

@Table(name = "JOB_POSTING_APPLICATIONS")
@Inheritance(strategy = SINGLE_TABLE)
@Entity
@DiscriminatorColumn(name = "JOB_POSTING_APPLICATION_TYPE", length = 100)
@EntityListeners(AbsoluteStartTimeAdjuster.class)
public abstract class JobPostingApplication extends AuditedEntity implements HasAbsoluteStartTime {

    /**
     * {@link #status} for a new {@link JobPostingApplication}.
     */
    public static final String NEW = "NEW";

    /**
     * {@link #status} for {@link #ACCEPTED} {@link JobPostingApplication}, which then was cancelled by {@link PracticeOwner}.
     */
    public static final String CANCELLED = "CANCELLED";

    /**
     * {@link #status} for a previously {@link #NEW} {@link JobPostingApplication} which was booked by {@link PracticeOwner}.
     */
    public static final String BOOKED = "BOOKED";

    /**
     * {@link #status} for a previously {@link #BOOKED}/{@link #ACCEPTED} {@link JobPostingApplication}, which then was rejected by {@link Professional}.
     */
    public static final String REJECTED = "REJECTED";

    /**
     * {@link #status} for a previously {@link #BOOKED} {@link JobPostingApplication}, which then was accepted by {@link Professional}.
     */
    public static final String ACCEPTED = "ACCEPTED";

    /**
     * {@link #status} for a previously {@link #NEW} {@link JobPostingApplication}, which then was cancelled as a consequence of accepting a {@link #BOOKED} application by <b>other {@link Professional}</b>.
     */
    public static final String CANCELLED_DURING_ACCEPTING_CONCURRENT_APPLICATION = "CANCELLED_DURING_ACCEPTING_CONCURRENT_APPLICATION";

    /**
     * {@link #status} for a previously {@link #ACCEPTED} {@link JobPostingApplication}, which then was cancelled as a consequence of update of {@link JobPosting} by <b>{@link PracticeOwner}</b>, and at that moment already had one  {@link JobDay#CHECKED_IN CHECKED_IN} {@link JobDay}.
     */
    public static final String PREMATURELY_COMPLETED = "PREMATURELY_COMPLETED";

    /**
     * {@link #status} for a previously {@link #ACTIVE_STATUSES active} {@link JobPostingApplication}, which then was cancelled as a consequence of update of {@link JobPosting} by <b>{@link PracticeOwner}</b>.
     */
    public static final String CANCELLED_DURING_JOB_POSTING_UPDATE = "CANCELLED_DURING_JOB_POSTING_UPDATE";

    /**
     * {@link #status} for a previously {@link #NEW active} {@link JobPostingApplication}, which then was cancelled as a consequence of cancel of {@link JobPosting} by <b>{@link PracticeOwner}</b>.
     */
    public static final String NEW_CANCELLED_DURING_JOB_POSTING_CANCEL = "NEW_CANCELLED_DURING_JOB_POSTING_CANCEL";

    /**
     * {@link #status} for a previously {@link #BOOKED active} {@link JobPostingApplication}, which then was cancelled as a consequence of cancel of {@link JobPosting} by <b>{@link PracticeOwner}</b>.
     */
    public static final String BOOKED_CANCELLED_DURING_JOB_POSTING_CANCEL = "BOOKED_CANCELLED_DURING_JOB_POSTING_CANCEL";

    /**
     * {@link #status} for a previously {@link #ACCEPTED active} {@link JobPostingApplication}, which then was cancelled as a consequence of cancel of {@link JobPosting} by <b>{@link PracticeOwner}</b>.
     */
    public static final String ACCEPTED_CANCELLED_DURING_JOB_POSTING_CANCEL = "ACCEPTED_CANCELLED_DURING_JOB_POSTING_CANCEL";

    /**
     * {@link #status} for a previously {@link #ACCEPTED} {@link JobPostingApplication}, which then was completed.
     */
    public static final String COMPLETED = "COMPLETED";

    public static final Set<String> ACTIVE_STATUSES = of(NEW, BOOKED, ACCEPTED);

    public static final Set<String> INACTIVE_STATUSES = of(REJECTED, CANCELLED_DURING_ACCEPTING_CONCURRENT_APPLICATION, CANCELLED_DURING_JOB_POSTING_UPDATE, PREMATURELY_COMPLETED);

    @Column(name = "status", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "fk_professional_id", nullable = false)
    private Professional professional;

    @ManyToOne
    @JoinColumn(name = "fk_job_posting_id", nullable = false, insertable = false, updatable = false)
    private JobPosting jobPosting;

    @Column(name = "zoned_start_date_time", nullable = false)
    private ZonedDateTime zonedStartDateTime;

    public JobPosting getJobPosting() {
        return jobPosting;
    }

    /**
     * Only for abstract query purposes
     */
    @Deprecated
    public void setJobPosting(JobPosting jobPosting) {
        this.jobPosting = jobPosting;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
    }

    @Override
    public LocalDate getStartDate() {
        return getJobPosting().getStartDate();
    }

    @Override
    public LocalTime getStartTime() {
        return LocalTime.MIN;
    }

    @Override
    public ZoneId getTimeZone() {
        return getJobPosting().getLocation().getTimeZone();
    }

    @Override
    public void setZonedStartDateTime(ZonedDateTime zonedStartDateTime) {
        this.zonedStartDateTime = zonedStartDateTime;
    }

    @Override
    public ZonedDateTime getZonedStartDateTime() {
        return this.zonedStartDateTime;
    }
}
