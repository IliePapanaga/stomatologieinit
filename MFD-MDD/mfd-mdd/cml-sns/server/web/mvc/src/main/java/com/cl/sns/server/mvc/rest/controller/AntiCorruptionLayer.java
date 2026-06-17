package com.cl.sns.server.mvc.rest.controller;

import com.cl.sns.server.core.model.api.notification.SendNotificationRequest;
import com.cl.sns.server.core.model.api.template.CreateNotificationTemplate;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModelList;
import com.cl.sns.server.core.model.db.msg.NotificationTemplate;
import com.cl.sns.server.mvc.rest.controller.mapper.NotificationTemplateMapper;
import com.cl.sns.server.mvc.rest.controller.mapper.SendNotificationRequestMapper;
import com.cl.sns.server.mvc.rest.controller.model.notification.SendNotificationRequestDTO;
import com.cl.sns.server.mvc.rest.controller.model.templates.CreateNotificationTemplateDTO;
import com.cl.sns.server.mvc.rest.controller.model.templates.NotificationTemplateDTO;
import com.cl.sns.server.mvc.rest.controller.model.templates.NotificationTemplateListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Anti corruption layer
 * <p />
 * Converts DTOs to Models & Models to DTOs
 */
@Component
public class AntiCorruptionLayer {

    @Autowired
    private NotificationTemplateMapper notificationTemplateMapper;

    @Autowired
    private SendNotificationRequestMapper sendNotificationRequestMapper;

    public CreateNotificationTemplate convert(CreateNotificationTemplateDTO createNotificationTemplateDTO){
        return notificationTemplateMapper.convert(createNotificationTemplateDTO);
    }

    public NotificationTemplateModel convert(NotificationTemplateDTO notificationTemplateDTO) {
        return notificationTemplateMapper.convert(notificationTemplateDTO);
    }

    public NotificationTemplateDTO convert(NotificationTemplateModel notificationTemplateModel){
        return notificationTemplateMapper.convert(notificationTemplateModel);
    }

    public SendNotificationRequest convert(SendNotificationRequestDTO requestDTO){
       return sendNotificationRequestMapper.convert(requestDTO);
    }

    public NotificationTemplateListDTO convert(NotificationTemplateModelList notificationTemplateModelList) {
        NotificationTemplateListDTO result = new NotificationTemplateListDTO();

        result.setTotalCount(notificationTemplateModelList.getTotalCount());
        result.setNotificationTemplates(notificationTemplateModelList.getModels().stream()
                .map(this::convert)
                .collect(Collectors.toList()));

        return result;
    }


}
