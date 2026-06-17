package com.cl.sns.server.mvc.rest.controller.model.templates;


import com.cl.sns.server.mvc.rest.controller.model.BaseDTO;


public abstract class BaseNotificationTemplateDTO extends BaseDTO{

    private String name;
    private String description;
    private String type;
    private String subject;
    private String content;
    private String transport;

    public BaseNotificationTemplateDTO() {
    }

    public String getName() {
        return name;
    }

    public BaseNotificationTemplateDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public BaseNotificationTemplateDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public BaseNotificationTemplateDTO setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getContent() {
        return content;
    }

    public BaseNotificationTemplateDTO setContent(String content) {
        this.content = content;
        return this;
    }

    public String getTransport() {
        return transport;
    }

    public BaseNotificationTemplateDTO setTransport(String transport) {
        this.transport = transport;
        return this;
    }

    public String getType() {
        return type;
    }

    public BaseNotificationTemplateDTO setType(String type) {
        this.type = type;
        return this;
    }
}
