package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;
import com.cl.mdd.server.core.validation.constraint.JobInterviewValidOptions;
import com.cl.mdd.server.core.validation.group.Complexity;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@ExpressionConstraint(expression = "not @permanentJobPostingApplicationDao.hasActiveInterviews(#this?.applicationId)", message = "{job.interview.already.exists}")
@JobInterviewValidOptions(groups = Complexity.High.class, message = "{job.interview.invalid.option}")
public class ScheduleJobInterview extends MDDModel {

    @NotNull(message = "{application.id.not.null}")
    private String applicationId;

    private boolean working;

    @Length(message = "{job.interview.comments.length}", max = 255)
    private String comments;

    @Valid
    @Size(max = 4, min = 4, message = "{job.interview.options.size}")
    private List<JobInterviewScheduleOption> options;

    public String getApplicationId() {
        return applicationId;
    }

    public ScheduleJobInterview setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public boolean isWorking() {
        return working;
    }

    public ScheduleJobInterview setWorking(boolean working) {
        this.working = working;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public ScheduleJobInterview setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public List<JobInterviewScheduleOption> getOptions() {
        return options;
    }

    public ScheduleJobInterview setOptions(List<JobInterviewScheduleOption> options) {
        this.options = options;
        return this;
    }
}
