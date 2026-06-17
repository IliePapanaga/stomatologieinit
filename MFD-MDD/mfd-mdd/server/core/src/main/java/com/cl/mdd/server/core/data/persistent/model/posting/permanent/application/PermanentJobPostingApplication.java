package com.cl.mdd.server.core.data.persistent.model.posting.permanent.application;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.PermanentJobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import com.google.common.collect.Sets;

import javax.persistence.*;
import java.util.Set;

@Entity
@DiscriminatorValue("PERMANENT_JOB_POSTING_APPLICATION")
public class PermanentJobPostingApplication extends JobPostingApplication {

    public static final String NOT_BOOKING = "NOT_BOOKING";

    public static final String BOOKING_APPLIED = "BOOKING_APPLIED";

    public static final String BOOKING_OFFER_SENT = "BOOKING_OFFER_SENT";

    public static final String BOOKING_FILLED = "BOOKING_FILLED";

    @ManyToOne
    @JoinColumn(name = "fk_job_posting_id", nullable = false)
    private PermanentJobPosting permanentJobPosting;

    @OneToMany(mappedBy = "application", cascade = {CascadeType.REMOVE})
    private Set<JobInterview> interviews = Sets.newHashSet();

    public PermanentJobPosting getPermanentJobPosting() {
        return permanentJobPosting;
    }

    public void setPermanentJobPosting(PermanentJobPosting permanentJobPosting) {
        this.permanentJobPosting = permanentJobPosting;
    }

    public Set<JobInterview> getInterviews() {
        return interviews;
    }

    public PermanentJobPostingApplication setInterviews(Set<JobInterview> interviews) {
        this.interviews = interviews;
        return this;
    }

    @Override
    public JobPosting getJobPosting() {
        return getPermanentJobPosting();
    }
}
