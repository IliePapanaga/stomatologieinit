package com.cl.sns.server.core.model.api.template;

import com.cl.sns.server.core.validation.groups.Update;

import javax.validation.constraints.NotNull;

public class NotificationTemplateModel extends BaseNotificationTemplateModel {

    @NotNull(message = "{notification.template.id.not.null}", groups = Update.class)
    private String id;

    public String getId() {
        return id;
    }

    public NotificationTemplateModel setId(String id) {
        this.id = id;
        return this;
    }
}
