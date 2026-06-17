package com.cl.mdd.server.core.data.persistent.model.user;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.contact.Contact;

import javax.persistence.*;
import java.time.ZonedDateTime;

import static javax.persistence.InheritanceType.SINGLE_TABLE;

@Table(name = "USERS")
@Inheritance(strategy = SINGLE_TABLE)
@Entity
@DiscriminatorColumn(name = "USER_TYPE")
public abstract class User extends AuditedEntity {

    public static final String ROLE_SUPER_USER = "ROLE_SUPER_USER";

    public static final String ROLE_SYSTEM_USER = "ROLE_SYSTEM_USER";

    public static final String ROLE_PREVIOUS_SYSTEM_USER = "ROLE_PREVIOUS_SYSTEM_USER";

    public static final String ROLE_PRACTICE_OWNER = "ROLE_PRACTICE_OWNER";

    public static final String ROLE_PROFESSIONAL = "ROLE_PROFESSIONAL";

    public static final String ACTIVE = "ACTIVE";

    public static final String EMAIL_CONFIRMATION_PENDING = "EMAIL_CONFIRMATION_PENDING";

    public static final String INACTIVE = "INACTIVE";

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "fk_contact_id", updatable = false)
    private Contact contact;

    @Column(name = "last_activity_date")
    private ZonedDateTime lastActivity;

    @ManyToOne
    @JoinColumn(name = "fk_approved_by_user_id")
    private SystemUser approvedBy;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public ZonedDateTime getLastActivity() {
        return lastActivity;
    }

    public User setLastActivity(ZonedDateTime lastActivity) {
        this.lastActivity = lastActivity;
        return this;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public User setApprovedBy(SystemUser approvedBy) {
        this.approvedBy = approvedBy;
        return this;
    }
}
