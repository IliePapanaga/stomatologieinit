package com.cl.sns.server.core.model.api.template;

import com.cl.sns.server.core.model.api.BaseModel;
import com.cl.sns.server.core.validation.constraint.EmailNotificationTemplate;
import com.cl.sns.server.core.validation.constraint.NotificationTemplateUnique;
import com.cl.sns.server.core.validation.constraint.NotificationTransport;
import com.cl.sns.server.core.validation.constraint.SmsNotificationTemplate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NotificationTemplateUnique
@EmailNotificationTemplate
@SmsNotificationTemplate
public abstract class BaseNotificationTemplateModel extends BaseModel{

//    @NotNull(message = "{notification.template.name.not.null}")
    @Size(min = 1, max = 255, message = "{notification.template.name.length}")
    private String name;

    @Size(max = 1000, message = "{notification.template.description.length}")
    private String description;

    @NotNull(message = "{notification.template.type.not.null}")
    @Size(min = 1, max = 255, message = "{notification.template.type.length}")
    private String type;

    @Size(min = 1, max = 255, message = "{notification.template.subject.length}")
    private String subject;

    @NotNull(message = "{notification.template.content.not.null}")
    @Size(min = 1, max = 4000, message = "{notification.template.content.length}")
    private String content;

    @NotNull(message = "{notification.template.transport.not.null}")
    @NotificationTransport
    private String transport;

    public String getName() {
        return name;
    }

    public BaseNotificationTemplateModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public BaseNotificationTemplateModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public BaseNotificationTemplateModel setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getContent() {
        return content;
    }

    public BaseNotificationTemplateModel setContent(String content) {
        this.content = content;
        return this;
    }

    public String getTransport() {
        return transport;
    }

    public BaseNotificationTemplateModel setTransport(String transport) {
        this.transport = transport;
        return this;
    }

    public String getType() {
        return type;
    }

    public BaseNotificationTemplateModel setType(String type) {
        this.type = type;
        return this;
    }
}
