package com.cl.mdd.server.core.data.model;

import org.hibernate.validator.constraints.Length;

public class PracticeOwnerModel extends UserModel {

    @Length(max = 1000, message = "{practice.owner.comments.length}")
    private String comments;

    public String getComments() {
        return comments;
    }

    public PracticeOwnerModel setComments(String comments) {
        this.comments = comments;
        return this;
    }
}
