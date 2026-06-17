package com.cl.mdd.server.core.data.model.common;

import com.cl.mdd.server.core.data.model.MDDModel;
import com.cl.mdd.server.core.validation.constraint.composite.Email;
import com.cl.mdd.server.core.validation.constraint.composite.Phone;
import com.cl.mdd.server.core.validation.group.Register;
import com.cl.mdd.server.core.validation.group.Save;
import com.cl.mdd.server.core.validation.group.Update;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ContactModel extends MDDModel {

    @Valid
    @NotNull(groups = {Register.class, Save.class, Update.class}, message = "{name.not.null}")
    private FullNameModel name;

    @Email
    @NotNull(groups = {Save.class}, message = "{email.not.null}")
    private String email;

    @Phone
    @NotNull(groups = {Register.class, Save.class, Update.class}, message = "{phone.not.null}")
    private String phone;

    private String fax;

    @Valid
    @NotNull(groups = {Register.class, Save.class, Update.class})
    private AddressModel address;

    public String getEmail() {
        return email;
    }

    public ContactModel setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public ContactModel setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getFax() {
        return fax;
    }

    public ContactModel setFax(String fax) {
        this.fax = fax;
        return this;
    }

    public FullNameModel getName() {
        return name;
    }

    public ContactModel setName(FullNameModel name) {
        this.name = name;
        return this;
    }

    public AddressModel getAddress() {
        return address;
    }

    public ContactModel setAddress(AddressModel address) {
        this.address = address;
        return this;
    }
}
