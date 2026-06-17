package com.cl.mdd.server.core.data.persistent.model.posting.permanent;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.PermanentJobPostingApplication;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.LocalTime;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Entity
@DiscriminatorValue(PermanentJobPosting.DISCRIMINATOR)
public class PermanentJobPosting extends JobPosting {

    public static final String DISCRIMINATOR = "PERMANENT_JOB_POSTING";

    @OneToMany(mappedBy = "permanentJobPosting")
    private Set<PermanentJobPostingApplication> applications = newHashSet();

    public Set<PermanentJobPostingApplication> getApplications() {
        return applications;
    }

    public void setApplications(Set<PermanentJobPostingApplication> applications) {
        this.applications = applications;
    }

    @Override
    public LocalTime getStartTime() {
        return LocalTime.MIN;
    }
}
