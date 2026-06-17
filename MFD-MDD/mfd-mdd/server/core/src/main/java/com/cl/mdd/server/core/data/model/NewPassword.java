package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.composite.Password;

import javax.validation.constraints.NotNull;

public class NewPassword extends MDDModel {

    @NotNull(message = "{reset.password.token.not.null}")
    private String token;

    @Password
    @NotNull(message = "{password.not.null}")
    private String password;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
