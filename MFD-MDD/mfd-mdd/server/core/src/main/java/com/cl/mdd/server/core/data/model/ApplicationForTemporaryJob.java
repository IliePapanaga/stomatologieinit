package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;
import com.cl.mdd.server.core.validation.group.Complexity;
import com.google.common.collect.Sets;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@ExpressionConstraint(expression = "@temporaryJobPostingDao.getAvailableDays(#this.jobPostingId, #this.workingDays)" +
        ".?[#this.getZonedStartDateTime().isAfter((T(java.time.ZonedDateTime).now()))].size() > 0",
        groups = Complexity.High.class,
        message = "{job.posting.application.no.available.days}")
public class ApplicationForTemporaryJob extends MDDModel {

    @NotNull(message = "{job.posting.id.not.null}")
    private String jobPostingId;

    @NotEmpty(message = "{job.posting.application.days.not.empty}")
    private Set<LocalDate> workingDays = Sets.newHashSet();

    public String getJobPostingId() {
        return jobPostingId;
    }

    public void setJobPostingId(String jobPostingId) {
        this.jobPostingId = jobPostingId;
    }

    public Set<LocalDate> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Set<LocalDate> workingDays) {
        this.workingDays = workingDays;
    }
}
