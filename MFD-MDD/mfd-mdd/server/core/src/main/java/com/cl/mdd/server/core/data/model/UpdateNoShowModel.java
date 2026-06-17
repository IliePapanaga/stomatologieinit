package com.cl.mdd.server.core.data.model;

public class UpdateNoShowModel {
    private String id;
    private String comments;

    public UpdateNoShowModel() {
    }

    public UpdateNoShowModel(String id, String comments) {
        this.id = id;
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public UpdateNoShowModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public UpdateNoShowModel setComments(String comments) {
        this.comments = comments;
        return this;
    }
}
