package com.cl.mdd.server.core.data.persistent.model.payment;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.PermanentJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Payment.
 * <p />
 * {@link #location} and {@link #professional} are redundant, but convenient for easy access.
 */
@Entity
@Table(name = "PAYMENTS")
public class Payment extends AuditedEntity {

    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_FAILED_FINAL = "FAILED_FINAL";
    public static final String STATUS_CANCELED = "CANCELED";
    public static final String STATUS_PARTIAL = "PARTIAL";

    @ManyToOne
    @JoinColumn(name = "fk_practice_id", nullable = false, updatable = false)
    private Practice practice;

    @ManyToOne
    @JoinColumn(name = "fk_location_id", nullable = false, updatable = false)
    private PracticeLocation location;

    @ManyToOne
    @JoinColumn(name = "fk_pro_id", nullable = false, updatable = false)
    private Professional professional;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @OneToOne
    @JoinColumn(name = "fk_job_day_id", updatable = false)
    private JobDay jobDay;

    @OneToOne
    @JoinColumn(name = "fk_interview_id", updatable = false)
    private JobInterview jobInterview;

    @OneToOne
    @JoinColumn(name = "fk_permanent_application_id", updatable = false)
    private PermanentJobPostingApplication permanentJobApplication;

    @Column(name = "payment_status", nullable = false)
    private String status = STATUS_NEW;

    @Column(name = "attempts_elapsed")
    private LocalDateTime lastAttemptsElapsed;

    @Column(name = "attempts_round", nullable = false)
    private int currentRound;

    @Column(name = "last_method")
    private String method;

    @OneToOne(mappedBy = "payment")
    private PaymentLock lock;

    @OneToMany(mappedBy = "payment")
    private Collection<PaymentAttempt> attempts;

    public Practice getPractice() {
        return this.practice;
    }

    public void setPractice(Practice practice) {
        this.practice = practice;
    }

    public PracticeLocation getLocation() {
        return location;
    }

    public void setLocation(PracticeLocation location) {
        this.location = location;
    }

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public JobDay getJobDay() {
        return this.jobDay;
    }

    public void setJobDay(JobDay jobDay) {
        this.jobDay = jobDay;
    }

    public JobInterview getJobInterview() {
        return jobInterview;
    }

    public void setJobInterview(JobInterview jobInterview) {
        this.jobInterview = jobInterview;
    }

    public PermanentJobPostingApplication getPermanentJobApplication() {
        return permanentJobApplication;
    }

    public void setPermanentJobApplication(PermanentJobPostingApplication permanentJobApplication) {
        this.permanentJobApplication = permanentJobApplication;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastAttemptsElapsed() {
        return this.lastAttemptsElapsed;
    }

    public void setLastAttemptsElapsed(LocalDateTime lastAttemptsElapsed) {
        this.lastAttemptsElapsed = lastAttemptsElapsed;
    }

    public int getCurrentRound() {
        return this.currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public PaymentLock getLock() {
        return this.lock;
    }

    public void setLock(PaymentLock lock) {
        this.lock = lock;
    }

    public Collection<PaymentAttempt> getAttempts() {
        return attempts;
    }

    public void setAttempts(Collection<PaymentAttempt> attempts) {
        this.attempts = attempts;
    }
}
