package com.cl.sns.server.mvc.rest.controller.model.templates;


public class NotificationTemplateDTO extends BaseNotificationTemplateDTO {

    private String id;

    public String getId() {
        return id;
    }

    public NotificationTemplateDTO setId(String id) {
        this.id = id;
        return this;
    }
}
