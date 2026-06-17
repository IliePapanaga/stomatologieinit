package com.cl.sns.server.core.model.db.msg;

import com.cl.sns.server.core.model.db.common.Identity;

import javax.persistence.*;

@Entity
@Table(name = "NOTIFICATION_TEMPLATES",
        uniqueConstraints = @UniqueConstraint(name = "UNQ_NOTIFICATION_TEMPLATE", columnNames = {"type", "transport_id"}))
public class NotificationTemplate extends Identity {

    private static final long serialVersionUID = 1L;

    @Column(name = "name" /*, nullable = false*/)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Notification type
     */
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "subject")
    private String subject;

    @Column(name = "content", nullable = false, length = 4000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "transport_id", nullable = false)
    private MessagingTransport transport;

    public NotificationTemplate() {
    }

    public NotificationTemplate(String name, String description, String type, String subject, String content, MessagingTransport transport) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.subject = subject;
        this.content = content;
        this.transport = transport;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessagingTransport getTransport() {
        return transport;
    }

    public void setTransport(MessagingTransport transport) {
        this.transport = transport;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
