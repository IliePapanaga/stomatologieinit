package com.cl.mdd.server.core.data.model.common;

import com.cl.mdd.server.core.data.model.MDDModel;

public class EducationModel extends MDDModel {

    private String id;

    private String name;

    public String getName() {
        return name;
    }

    public EducationModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getId() {
        return id;
    }

    public EducationModel setId(String id) {
        this.id = id;
        return this;
    }
}
