package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.data.model.common.ContactModel;

import javax.validation.Valid;

public abstract class UserModel extends MDDModel {
    private String id;
    @Valid
    private ContactModel contact;

    public ContactModel getContact() {
        return contact;
    }

    public UserModel setContact(ContactModel contact) {
        this.contact = contact;
        return this;
    }

    public String getId() {
        return id;
    }

    public UserModel setId(String id) {
        this.id = id;
        return this;
    }
}
