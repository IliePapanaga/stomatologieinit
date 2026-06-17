package com.cl.sns.server.core.manager.mapper.impl;

import com.cl.sns.server.core.dao.MessagingTransportDao;
import com.cl.sns.server.core.manager.mapper.IModelConverter;
import com.cl.sns.server.core.model.api.template.BaseNotificationTemplateModel;
import com.cl.sns.server.core.model.api.template.CreateNotificationTemplate;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.core.model.db.msg.NotificationTemplate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Notification template converter
 * <p/>
 * Provides entity to model & model to entity conversion operations.
 */
@Component
public class NotificationTemplateModelConverter implements IModelConverter {

    @Autowired
    private MessagingTransportDao messagingTransportDao;

    public NotificationTemplate toBaseEntity(BaseNotificationTemplateModel model) {
        NotificationTemplate notificationTemplate = null;
        if (model == null) {
            return notificationTemplate;
        }

        notificationTemplate = new NotificationTemplate();
        notificationTemplate.setName(model.getName());
        notificationTemplate.setDescription(model.getDescription());
        notificationTemplate.setContent(model.getContent());
        notificationTemplate.setSubject(model.getSubject());
        notificationTemplate.setType(model.getType());

        String transport = model.getTransport();
        if (StringUtils.isNotBlank(transport)) {
            notificationTemplate.setTransport(messagingTransportDao.findByName(transport));
        }

        return notificationTemplate;
    }

    public NotificationTemplate toEntity(NotificationTemplateModel model) {
        NotificationTemplate notificationTemplate = toBaseEntity(model);

        if (notificationTemplate == null) {
            return null;
        }

        notificationTemplate.setId(model.getId());
        return notificationTemplate;
    }

    public NotificationTemplate toEntity(CreateNotificationTemplate model) {
        return toBaseEntity(model);
    }

    public NotificationTemplateModel toModel(NotificationTemplate entity) {
        NotificationTemplateModel model = null;

        if (entity == null) {
            return model;
        }

        model = new NotificationTemplateModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setSubject(entity.getSubject());
        model.setContent(entity.getContent());
        model.setType(entity.getType());
        model.setTransport(entity.getTransport().getName());

        return model;
    }

    public List<NotificationTemplateModel> toModels(List<NotificationTemplate> templates){
        return CollectionUtils.emptyIfNull(templates).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

}
