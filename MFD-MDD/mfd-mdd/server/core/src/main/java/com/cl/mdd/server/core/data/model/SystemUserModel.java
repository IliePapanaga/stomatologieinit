package com.cl.mdd.server.core.data.model;

import java.time.ZonedDateTime;

public class SystemUserModel extends UserModel {

    private ZonedDateTime modified;

    private String state;

    public ZonedDateTime getModified() {
        return modified;
    }

    public SystemUserModel setModified(ZonedDateTime modified) {
        this.modified = modified;
        return this;
    }

    public String getState() {
        return state;
    }

    public SystemUserModel setState(String state) {
        this.state = state;
        return this;
    }
}
