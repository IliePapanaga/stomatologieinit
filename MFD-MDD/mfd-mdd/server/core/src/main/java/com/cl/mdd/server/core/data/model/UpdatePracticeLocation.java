package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.group.Save;

import javax.validation.constraints.NotNull;

public class UpdatePracticeLocation extends AddPracticeLocation {

    @NotNull(groups = Save.class, message = "{practice.location.id.not.null}")
    private String id;

    public String getId() {
        return id;
    }

    public UpdatePracticeLocation setId(String id) {
        this.id = id;
        return this;
    }

}
