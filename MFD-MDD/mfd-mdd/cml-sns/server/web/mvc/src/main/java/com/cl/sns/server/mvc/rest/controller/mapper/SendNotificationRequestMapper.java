package com.cl.sns.server.mvc.rest.controller.mapper;

import com.cl.sns.server.core.model.api.notification.SendNotificationRequest;
import com.cl.sns.server.mvc.rest.controller.model.notification.SendNotificationRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SendNotificationRequestMapper {

    @Mappings({
            @Mapping(source = "notificationType", target = "notificationType"),
            @Mapping(source = "recipientDetails.email", target = "recipientDetails.email"),
            @Mapping(source = "recipientDetails.phone", target = "recipientDetails.phone"),
            @Mapping(source = "recipientDetails.placeHolders", target = "recipientDetails.placeHolders")
    })
    SendNotificationRequest convert(SendNotificationRequestDTO requestDTO);

}
