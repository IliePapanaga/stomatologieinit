package com.cl.mdd.server.core.data.persistent.model.practice;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.common.Speciality;
import com.cl.mdd.server.core.data.persistent.model.contact.Address;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.professional.BlackListedProfessional;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Entity
@Table(name = "PRACTICES")
public class Practice extends AuditedEntity {

    public static final String ACTIVE = "ACTIVE";

    public static final String INACTIVE = "INACTIVE";

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "second_email")
    private String secondEmail;

    @Column(name = "after_work_phone")
    private String afterWorkPhone;

    @Column(name = "web_site")
    private String webSite;

    @Column(name = "office_manager_name")
    private String officeManagerName;

    //TODO THINK IF WE NEED THIS FIELD
    @Column(name = "status")
    private String status;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @MapsId
    private PracticeOwner owner;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_billing_address_id", nullable = false, updatable = false)
    private Address billingAddress;

    @OneToMany(mappedBy = "practice")
    private Set<PracticeLocation> locations = newHashSet();

    @ManyToMany
    @JoinTable(name = "PRACTICE_SPECIALITIES",
            joinColumns = @JoinColumn(name = "fk_practice_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_speciality_id"))
    private Set<Speciality> specialities = newHashSet();

    @OneToMany(mappedBy = "practice", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BlackListedProfessional> blackListedProfessionals = newHashSet();

    @Column(name = "softwares")
    private String softwares;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSecondEmail() {
        return secondEmail;
    }

    public void setSecondEmail(String secondEmail) {
        this.secondEmail = secondEmail;
    }

    public String getAfterWorkPhone() {
        return afterWorkPhone;
    }

    public void setAfterWorkPhone(String afterWorkPhone) {
        this.afterWorkPhone = afterWorkPhone;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getOfficeManagerName() {
        return officeManagerName;
    }

    public void setOfficeManagerName(String officeManagerName) {
        this.officeManagerName = officeManagerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PracticeOwner getOwner() {
        return owner;
    }

    public void setOwner(PracticeOwner owner) {
        this.owner = owner;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getSoftwares() {
        return softwares;
    }

    public void setSoftwares(String softwares) {
        this.softwares = softwares;
    }

    public Set<PracticeLocation> getLocations() {
        return locations;
    }

    public void setLocations(Set<PracticeLocation> locations) {
        this.locations = locations;
    }

    public Set<Speciality> getSpecialities() {
        return specialities;
    }

    public void setSpecialities(Set<Speciality> specialities) {
        this.specialities = specialities;
    }

    public Set<BlackListedProfessional> getBlackListedProfessionals() {
        return blackListedProfessionals;
    }

    public Practice setBlackListedProfessionals(Set<BlackListedProfessional> blackListedProfessionals) {
        this.blackListedProfessionals = blackListedProfessionals;
        return this;
    }
}
