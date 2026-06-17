package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.posting.NotStarted;
import com.cl.mdd.server.core.validation.group.Update;

import javax.validation.constraints.NotNull;

public class SimplePermanentJobPosting extends PublishSimplePermanentJobPosting implements JobPosting {

    @NotNull(message = "{job.posting.id.not.null}")
    @NotStarted(message = "{job.posting.already.started}", groups = Update.class)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
