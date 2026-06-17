package com.cl.sns.server.mvc.rest.controller.mapper;

import com.cl.sns.server.core.model.api.template.CreateNotificationTemplate;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.mvc.rest.controller.model.templates.CreateNotificationTemplateDTO;
import com.cl.sns.server.mvc.rest.controller.model.templates.NotificationTemplateDTO;
import org.springframework.stereotype.Component;

/*
@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-10T11:57:05+0200",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
*/
@Component
public class NotificationTemplateMapperImpl implements NotificationTemplateMapper {

    @Override
    public CreateNotificationTemplate convert(CreateNotificationTemplateDTO notificationTemplateDTO) {
        if ( notificationTemplateDTO == null ) {
            return null;
        }

        CreateNotificationTemplate createNotificationTemplate = new CreateNotificationTemplate();

        createNotificationTemplate.setName( notificationTemplateDTO.getName() );
        createNotificationTemplate.setDescription( notificationTemplateDTO.getDescription() );
        createNotificationTemplate.setTransport( notificationTemplateDTO.getTransport() );
        createNotificationTemplate.setType( notificationTemplateDTO.getType() );
        createNotificationTemplate.setSubject( notificationTemplateDTO.getSubject() );
        createNotificationTemplate.setContent( notificationTemplateDTO.getContent() );

        return createNotificationTemplate;
    }

    @Override
    public NotificationTemplateModel convert(NotificationTemplateDTO notificationTemplateDTO) {
        if ( notificationTemplateDTO == null ) {
            return null;
        }

        NotificationTemplateModel notificationTemplateModel = new NotificationTemplateModel();

        notificationTemplateModel.setSubject( notificationTemplateDTO.getSubject() );
        notificationTemplateModel.setName( notificationTemplateDTO.getName() );
        notificationTemplateModel.setDescription( notificationTemplateDTO.getDescription() );
        notificationTemplateModel.setTransport( notificationTemplateDTO.getTransport() );
        notificationTemplateModel.setId( notificationTemplateDTO.getId() );
        notificationTemplateModel.setType( notificationTemplateDTO.getType() );
        notificationTemplateModel.setContent( notificationTemplateDTO.getContent() );

        return notificationTemplateModel;
    }

    @Override
    public NotificationTemplateDTO convert(NotificationTemplateModel notificationTemplate) {
        if ( notificationTemplate == null ) {
            return null;
        }

        NotificationTemplateDTO notificationTemplateDTO = new NotificationTemplateDTO();

        notificationTemplateDTO.setSubject( notificationTemplate.getSubject() );
        notificationTemplateDTO.setName( notificationTemplate.getName() );
        notificationTemplateDTO.setDescription( notificationTemplate.getDescription() );
        notificationTemplateDTO.setId( notificationTemplate.getId() );
        notificationTemplateDTO.setTransport( notificationTemplate.getTransport() );
        notificationTemplateDTO.setType( notificationTemplate.getType() );
        notificationTemplateDTO.setContent( notificationTemplate.getContent() );

        return notificationTemplateDTO;
    }
}
