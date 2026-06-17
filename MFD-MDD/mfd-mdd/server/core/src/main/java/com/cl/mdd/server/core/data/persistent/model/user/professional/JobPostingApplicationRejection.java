package com.cl.mdd.server.core.data.persistent.model.user.professional;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;

import javax.persistence.*;

@Entity
@Table(name = "JOB_POSTING_REJECTIONS")
public class JobPostingApplicationRejection extends AuditedEntity {

    public static final String REGISTERED = "REGISTERED";

    public static final String CLEARED = "CLEARED";

    @OneToOne(optional = false)
    @MapsId
    private JobPostingApplication jobPostingApplication;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "comments")
    private String comments;

    public String getComments() {
        return comments;
    }

    public JobPostingApplicationRejection setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public JobPostingApplicationRejection setStatus(String status) {
        this.status = status;
        return this;
    }

    public JobPostingApplication getJobPostingApplication() {
        return jobPostingApplication;
    }

    public JobPostingApplicationRejection setJobPostingApplication(JobPostingApplication jobPostingApplication) {
        this.jobPostingApplication = jobPostingApplication;
        return this;
    }
}
