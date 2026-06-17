package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.validation.constraint.composite.Password;
import com.cl.mdd.server.core.validation.constraint.composite.Username;
import com.cl.mdd.server.core.validation.group.Register;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public abstract class RegisterUser extends MDDModel {

    @Username
    @NotNull(groups = {Register.class}, message = "{username.not.null}")
    private String username;

    @Password(groups = {Register.class})
    @NotNull(groups = {Register.class}, message = "{password.not.null}")
    private String password;

    @Valid
    @NotNull
    private ContactModel contact;

    public ContactModel getContact() {
        return contact;
    }

    public RegisterUser setContact(ContactModel contact) {
        this.contact = contact;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public RegisterUser setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegisterUser setPassword(String password) {
        this.password = password;
        return this;
    }
}
