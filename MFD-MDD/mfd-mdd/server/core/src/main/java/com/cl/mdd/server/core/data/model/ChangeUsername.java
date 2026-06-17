package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.composite.Username;

import javax.validation.constraints.NotNull;

public class ChangeUsername {

    @Username
    @NotNull(message = "{username.not.null}")
    private String newUsername;

    @NotNull(message = "{password.not.null}")
    private String password;

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
