package com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.PermanentJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.FetchType.EAGER;

@Entity
@Table(name = "JOB_INTERVIEWS")
public class JobInterview extends AuditedEntity {

    /**
     * {@link JobInterview#status} for a newly created {@link JobInterview}
     */
    public static final String INVITED = "INVITED";

    /**
     * {@link JobInterview#status} for a {@link JobInterview} previously in status {@link JobInterview#INVITED}/{@link JobInterview#SCHEDULED} which was then rejected by {@link Professional}
     */
    public static final String REJECTED = "REJECTED";

    /**
     * {@link JobInterview#status} for a {@link JobInterview} previously in status {@link JobInterview#INVITED}/{@link JobInterview#SCHEDULED} which was then cancelled by {@link PracticeOwner}
     */
    public static final String CANCELLED = "CANCELLED";

    /**
     * {@link JobInterview#status} for a {@link JobInterview} previously in status {@link JobInterview#INVITED} which was then accepted by {@link Professional}
     */
    public static final String SCHEDULED = "SCHEDULED";

    /**
     * {@link JobInterview#status} for a {@link JobInterview} previously in status {@link JobInterview#SCHEDULED} which was then completed by the system(cron job).
     */
    public static final String COMPLETED = "COMPLETED";

    /**
     * {@link JobInterview#type} which means that the {@link JobInterview} is a working interview and should be paid as a {@link JobDay}
     */
    public static final String WORKING = "WORKING";

    /**
     * {@link JobInterview#type} which means that the {@link JobInterview} is a personal interview
     */
    public static final String PERSONAL = "PERSONAL";

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "comments")
    private String comments;

    @Column(name = "notified_start_soon", nullable = false)
    private boolean notifiedAboutStartSoon;

    @Column(name = "notified_finished", nullable = false)
    private boolean notifiedAboutFinished;

    @ManyToOne
    @JoinColumn(name = "fk_job_posting_application_id", nullable = false)
    private PermanentJobPostingApplication application;

    @OneToMany(mappedBy = "jobInterview", fetch = EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderColumn(name = "option_index")
    private List<JobInterviewOption> jobInterviewOptions = Lists.newArrayList();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = EAGER)
    @JoinColumn(name = "fk_accepted_job_interview_option_id")
    private JobInterviewOption acceptedOption;

    public String getStatus() {
        return status;
    }

    public JobInterview setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getType() {
        return type;
    }

    public JobInterview setType(String type) {
        this.type = type;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public JobInterview setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public boolean isNotifiedAboutStartSoon() {
        return notifiedAboutStartSoon;
    }

    public JobInterview setNotifiedAboutStartSoon(boolean notifiedAboutStartSoon) {
        this.notifiedAboutStartSoon = notifiedAboutStartSoon;
        return this;
    }

    public boolean isNotifiedAboutFinished() {
        return notifiedAboutFinished;
    }

    public JobInterview setNotifiedAboutFinished(boolean notifiedAboutFinished) {
        this.notifiedAboutFinished = notifiedAboutFinished;
        return this;
    }

    public PermanentJobPostingApplication getApplication() {
        return application;
    }

    public JobInterview setApplication(PermanentJobPostingApplication application) {
        this.application = application;
        return this;
    }

    public List<JobInterviewOption> getJobInterviewOptions() {
        return jobInterviewOptions;
    }

    public JobInterview setJobInterviewOptions(List<JobInterviewOption> jobInterviewOptions) {
        this.jobInterviewOptions = jobInterviewOptions;
        return this;
    }

    public JobInterviewOption getAcceptedOption() {
        return acceptedOption;
    }

    public JobInterview setAcceptedOption(JobInterviewOption acceptedOption) {
        this.acceptedOption = acceptedOption;
        return this;
    }
}
