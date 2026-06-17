package com.cl.sns.server.core.model.api.template;

import com.cl.sns.server.core.model.api.BaseModel;

import java.util.List;

public class NotificationTemplateModelList extends BaseModel {

    private Long totalCount;

    private List<NotificationTemplateModel> models;

    public NotificationTemplateModelList() {
    }

    public NotificationTemplateModelList(Long totalCount, List<NotificationTemplateModel> models) {
        this.totalCount = totalCount;
        this.models = models;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<NotificationTemplateModel> getModels() {
        return models;
    }

    public void setModels(List<NotificationTemplateModel> models) {
        this.models = models;
    }
}
