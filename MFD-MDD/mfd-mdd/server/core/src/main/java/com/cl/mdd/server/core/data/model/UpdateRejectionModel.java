package com.cl.mdd.server.core.data.model;

public class UpdateRejectionModel {

    private String id;

    private String comments;

    public UpdateRejectionModel() {
    }

    public UpdateRejectionModel(String id, String comments) {
        this.id = id;
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public UpdateRejectionModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public UpdateRejectionModel setComments(String comments) {
        this.comments = comments;
        return this;
    }
}
