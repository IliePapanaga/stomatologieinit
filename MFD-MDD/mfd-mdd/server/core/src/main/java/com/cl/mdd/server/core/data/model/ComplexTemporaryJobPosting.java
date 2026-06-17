package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.posting.NotStarted;
import com.cl.mdd.server.core.validation.group.Update;

import javax.validation.constraints.NotNull;

import static com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting.JOB_DAY_STRATEGY_COMPLEX;

public class ComplexTemporaryJobPosting extends PublishComplexTemporaryJobPosting {

    @NotNull(message = "{job.posting.id.not.null}")
    @NotStarted(message = "{job.posting.already.started}", groups = Update.class)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobDayStrategy() {
        return JOB_DAY_STRATEGY_COMPLEX;
    }
}
