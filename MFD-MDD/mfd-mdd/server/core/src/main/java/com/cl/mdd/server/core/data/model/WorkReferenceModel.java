package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.composite.Email;
import com.cl.mdd.server.core.validation.constraint.composite.Phone;
import com.cl.mdd.server.core.validation.group.Save;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class WorkReferenceModel extends MDDModel {

    @NotNull(groups = Save.class, message = "{workReference.name.not.null}")
    @Length(max = 255, groups = Save.class, message = "{workReference.name.length}")
    private String name;

    @Email(groups = Save.class)
    private String email;

    @Phone(groups = Save.class)
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
