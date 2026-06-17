package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.composite.Password;

import javax.validation.constraints.NotNull;

public class ChangePassword {

    @NotNull(message = "{password.not.null}")
    private String oldPassword;

    @Password
    @NotNull(message = "{password.new.not.null}")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
