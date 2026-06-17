package com.cl.mdd.server.core.data.persistent.model.contact;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.common.embeddable.FullName;

import javax.persistence.*;

@Entity
@Table(name = "CONTACTS")
public class Contact extends AuditedEntity {

    @Embedded
    private FullName name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "fax")
    private String fax;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_address_id", nullable = false)
    private Address address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_contact_photo_id")
    private ContactPhoto contactPhoto;

    public FullName getName() {
        return name;
    }

    public void setName(FullName name) {
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

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ContactPhoto getContactPhoto() {
        return contactPhoto;
    }

    public void setContactPhoto(ContactPhoto contactPhoto) {
        this.contactPhoto = contactPhoto;
    }
}
