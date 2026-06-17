package com.cl.mdd.server.core.data.persistent.model.posting.temporary.application;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting;

import javax.persistence.*;
import java.util.Set;

import static com.google.api.client.util.Sets.newHashSet;

@Entity
@DiscriminatorValue("TEMPORARY_JOB_POSTING_APPLICATION")
public class TemporaryJobPostingApplication extends JobPostingApplication {

    @ManyToMany
    @JoinTable(name = "TEMPORARY_JOB_POSTING_APPLICATION_JOB_DAYS",
            joinColumns = {@JoinColumn(name = "fk_application_id")},
            inverseJoinColumns = {@JoinColumn(name = "fk_job_day_id")})
    private Set<JobDay> jobDays = newHashSet();

    @ManyToOne
    @JoinColumn(name = "fk_job_posting_id", nullable = false)
    private TemporaryJobPosting temporaryJobPosting;

    public TemporaryJobPosting getTemporaryJobPosting() {
        return temporaryJobPosting;
    }

    public void setTemporaryJobPosting(TemporaryJobPosting temporaryJobPosting) {
        this.temporaryJobPosting = temporaryJobPosting;
    }

    public Set<JobDay> getJobDays() {
        return jobDays;
    }

    public void setJobDays(Set<JobDay> jobDays) {
        this.jobDays = jobDays;
    }

    @Override
    public JobPosting getJobPosting() {
        return getTemporaryJobPosting();
    }
}
