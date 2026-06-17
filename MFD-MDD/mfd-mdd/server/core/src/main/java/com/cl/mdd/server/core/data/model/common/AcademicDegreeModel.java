package com.cl.mdd.server.core.data.model.common;

import com.cl.mdd.server.core.data.model.MDDModel;

public class AcademicDegreeModel extends MDDModel {

    private String id;

    private String name;

    public String getName() {
        return name;
    }

    public AcademicDegreeModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getId() {
        return id;
    }

    public AcademicDegreeModel setId(String id) {
        this.id = id;
        return this;
    }
}
