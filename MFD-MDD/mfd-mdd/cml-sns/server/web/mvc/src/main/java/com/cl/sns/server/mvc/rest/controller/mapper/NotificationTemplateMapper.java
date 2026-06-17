package com.cl.sns.server.mvc.rest.controller.mapper;

import com.cl.sns.server.core.model.api.template.CreateNotificationTemplate;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.core.model.db.msg.NotificationTemplate;
import com.cl.sns.server.mvc.rest.controller.model.templates.CreateNotificationTemplateDTO;
import com.cl.sns.server.mvc.rest.controller.model.templates.NotificationTemplateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface NotificationTemplateMapper {

    @Mappings({
            @Mapping(source = "type", target = "type"),
            @Mapping(source = "subject", target = "subject"),
            @Mapping(source = "content", target = "content"),
            @Mapping(source = "transport", target = "transport"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "description", target = "description")
    })
    CreateNotificationTemplate convert(CreateNotificationTemplateDTO notificationTemplateDTO);

    @Mappings({
            @Mapping(source = "type", target = "type"),
            @Mapping(source = "subject", target = "subject"),
            @Mapping(source = "content", target = "content"),
            @Mapping(source = "transport", target = "transport"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "id", target = "id")
    })
    NotificationTemplateModel convert(NotificationTemplateDTO notificationTemplateDTO);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "type", target = "type"),
            @Mapping(source = "subject", target = "subject"),
            @Mapping(source = "content", target = "content"),
            @Mapping(source = "transport", target = "transport"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "description", target = "description")
    })
    NotificationTemplateDTO convert(NotificationTemplateModel notificationTemplate);

}
