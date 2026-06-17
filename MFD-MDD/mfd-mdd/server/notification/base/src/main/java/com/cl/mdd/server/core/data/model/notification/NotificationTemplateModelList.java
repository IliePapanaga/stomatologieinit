package com.cl.mdd.server.core.data.model.notification;

import java.util.List;

public class NotificationTemplateModelList {

    private Long totalCount;

    private List<NotificationTemplateModel> templates;

    public NotificationTemplateModelList() {
    }

    public NotificationTemplateModelList(List<NotificationTemplateModel> templates, Long totalCount) {
        this.totalCount = totalCount;
        this.templates = templates;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<NotificationTemplateModel> getTemplates() {
        return templates;
    }

    public void setTemplates(List<NotificationTemplateModel> templates) {
        this.templates = templates;
    }
}
