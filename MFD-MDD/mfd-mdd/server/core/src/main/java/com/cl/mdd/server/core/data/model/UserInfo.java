package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.data.model.common.FullNameModel;

import java.util.Set;

/**
 * User info which cares some information about currently authenticated user.
 * Exposes some non-sensitive data
 */
public class UserInfo extends MDDModel {

    private String id;

    private String username;

    private Set<String> roles;

    private String status;

    private FullNameModel name;

    private UserInfo realUser;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setName(FullNameModel name) {
        this.name = name;
    }

    public FullNameModel getName() {
        return name;
    }

    public UserInfo getRealUser() {
        return realUser;
    }

    public void setRealUser(UserInfo realUser) {
        this.realUser = realUser;
    }

    public String getId() {
        return id;
    }

    public UserInfo setId(String id) {
        this.id = id;
        return this;
    }
}
