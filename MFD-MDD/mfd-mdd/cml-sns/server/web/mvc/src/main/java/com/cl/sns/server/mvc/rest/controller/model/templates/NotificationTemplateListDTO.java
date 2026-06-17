package com.cl.sns.server.mvc.rest.controller.model.templates;


import com.cl.sns.server.mvc.rest.controller.model.BaseDTO;

import java.util.List;

public class NotificationTemplateListDTO extends BaseDTO {

    private Long totalCount;

    private List<NotificationTemplateDTO> notificationTemplates;

    public Long getTotalCount() {
        return totalCount;
    }

    public NotificationTemplateListDTO setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public List<NotificationTemplateDTO> getNotificationTemplates() {
        return notificationTemplates;
    }

    public NotificationTemplateListDTO setNotificationTemplates(List<NotificationTemplateDTO> notificationTemplates) {
        this.notificationTemplates = notificationTemplates;
        return this;
    }
}
