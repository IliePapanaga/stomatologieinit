package com.cl.sns.server.core.model.db.msg;


import com.cl.sns.server.core.model.db.common.Identity;

import javax.persistence.*;

/**
 * Messaging transport entity
 * <p/>
 */
@Entity
@Table(name = "MESSAGING_TRANSPORTS",
        uniqueConstraints = @UniqueConstraint(name = "UNQ_TRANSPORT_NAME", columnNames = {"name"}))
public class MessagingTransport extends Identity {

    @Basic
    private String name;

    public MessagingTransport() {
    }

    public MessagingTransport(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
