package com.cl.mdd.server.core.data.model.notification;

import java.util.Objects;

public class NotificationTemplateModel {

    private String id;

    private String name;

    private String description;
    
    private String type;

    private String subject;

    private String content;

    private String transport;

    public String getId() {
        return id;
    }

    public NotificationTemplateModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public NotificationTemplateModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public NotificationTemplateModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getType() {
        return type;
    }

    public NotificationTemplateModel setType(String type) {
        this.type = type;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public NotificationTemplateModel setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getContent() {
        return content;
    }

    public NotificationTemplateModel setContent(String content) {
        this.content = content;
        return this;
    }

    public String getTransport() {
        return transport;
    }

    public NotificationTemplateModel setTransport(String transport) {
        this.transport = transport;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTemplateModel that = (NotificationTemplateModel) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(type, that.type) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(content, that.content) &&
                Objects.equals(transport, that.transport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, type, subject, content, transport);
    }
}
