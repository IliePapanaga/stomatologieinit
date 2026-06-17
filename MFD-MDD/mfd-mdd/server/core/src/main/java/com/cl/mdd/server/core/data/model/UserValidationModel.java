package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.composite.Username;
import com.cl.mdd.server.core.validation.group.Email;

import javax.validation.constraints.NotNull;

public class UserValidationModel extends MDDModel {

    @NotNull(groups = Email.class)
    @Username(groups = Email.class)
    private String email;

    public String getEmail() {
        return email;
    }

    public UserValidationModel setEmail(String email) {
        this.email = email;
        return this;
    }
}
