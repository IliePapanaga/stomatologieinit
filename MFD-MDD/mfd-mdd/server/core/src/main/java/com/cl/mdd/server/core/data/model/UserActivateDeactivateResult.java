package com.cl.mdd.server.core.data.model;

/**
 * User activation - deactivation result model
 * <p/>
 */
public class UserActivateDeactivateResult extends MDDModel {

    private String id;
    private String status;

    public UserActivateDeactivateResult() {
    }

    public UserActivateDeactivateResult(String id, String status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public UserActivateDeactivateResult setId(String id) {
        this.id = id;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public UserActivateDeactivateResult setStatus(String status) {
        this.status = status;
        return this;
    }
}
