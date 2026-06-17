package com.cl.mdd.server.core.data.model.common;

import com.cl.mdd.server.core.data.model.MDDModel;

public class LanguageModel extends MDDModel {

    private String id;

    private String name;

    public String getName() {
        return name;
    }

    public LanguageModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getId() {
        return id;
    }

    public LanguageModel setId(String id) {
        this.id = id;
        return this;
    }
}
