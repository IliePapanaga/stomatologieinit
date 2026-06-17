package com.cl.mdd.server.core.data.model;

import javax.validation.constraints.NotNull;

import static com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting.JOB_DAY_STRATEGY_SIMPLE;

public class SimpleTemporaryJobPosting extends PublishSimpleTemporaryJobPosting {

    @NotNull(message = "{job.posting.id.not.null}")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobDayStrategy() {
        return JOB_DAY_STRATEGY_SIMPLE;
    }
}
