package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.data.model.common.AddressModel;
import com.cl.mdd.server.core.validation.constraint.Speciality;
import com.cl.mdd.server.core.validation.constraint.composite.Email;
import com.cl.mdd.server.core.validation.constraint.composite.Phone;
import com.cl.mdd.server.core.validation.constraint.composite.Website;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

public abstract class BasePracticeModel extends MDDModel {

    @Length(min = 1, max = 60, message = "{practice.name.length}")
    private String name;

    @Phone(message = "{practice.phone.invalid}")
    @NotNull(message = "{practice.phone.not.null}")
    private String phone;

    @Email(message = "{practice.second.email.invalid}")
    private String secondEmail;

    @Phone(message = "{practice.afterWork.phone.invalid}")
    private String afterWorkPhone;

    private String softwares;

    @Speciality
    private Set<String> specialities;

    @Website
    private String webSite;

    @Valid
    @NotNull
    private AddressModel billingAddress;

    @Length(min = 2, max = 60, message = "{practice.office.manager.name}")
    private String officeManagerName;


    public String getName() {
        return name;
    }

    public BasePracticeModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public BasePracticeModel setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getSecondEmail() {
        return secondEmail;
    }

    public BasePracticeModel setSecondEmail(String secondEmail) {
        this.secondEmail = secondEmail;
        return this;
    }

    public String getAfterWorkPhone() {
        return afterWorkPhone;
    }

    public BasePracticeModel setAfterWorkPhone(String afterWorkPhone) {
        this.afterWorkPhone = afterWorkPhone;
        return this;
    }

    public String getSoftwares() {
        return softwares;
    }

    public BasePracticeModel setSoftwares(String softwares) {
        this.softwares = softwares;
        return this;
    }

    public Set<String> getSpecialities() {
        return specialities;
    }

    public BasePracticeModel setSpecialities(Set<String> specialities) {
        this.specialities = specialities;
        return this;
    }

    public String getWebSite() {
        return webSite;
    }

    public BasePracticeModel setWebSite(String webSite) {
        this.webSite = webSite;
        return this;
    }

    public AddressModel getBillingAddress() {
        return billingAddress;
    }

    public BasePracticeModel setBillingAddress(AddressModel billingAddress) {
        this.billingAddress = billingAddress;
        return this;
    }

    public String getOfficeManagerName() {
        return officeManagerName;
    }

    public BasePracticeModel setOfficeManagerName(String officeManagerName) {
        this.officeManagerName = officeManagerName;
        return this;
    }

}
