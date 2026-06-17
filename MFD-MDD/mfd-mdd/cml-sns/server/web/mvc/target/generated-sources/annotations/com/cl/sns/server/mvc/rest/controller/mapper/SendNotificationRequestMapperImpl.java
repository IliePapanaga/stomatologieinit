package com.cl.sns.server.mvc.rest.controller.mapper;

import com.cl.sns.server.core.model.api.notification.RecipientDetails;
import com.cl.sns.server.core.model.api.notification.SendNotificationRequest;
import com.cl.sns.server.mvc.rest.controller.model.notification.SendNotificationRequestDTO;
import com.cl.sns.server.mvc.rest.controller.model.recipient.RecipientDetailsDTO;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/*
@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-10T11:57:05+0200",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
*/
@Component
public class SendNotificationRequestMapperImpl implements SendNotificationRequestMapper {

    @Override
    public SendNotificationRequest convert(SendNotificationRequestDTO requestDTO) {
        if ( requestDTO == null ) {
            return null;
        }

        SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();

        sendNotificationRequest.setRecipientDetails( recipientDetailsDTOToRecipientDetails( requestDTO.getRecipientDetails() ) );
        sendNotificationRequest.setNotificationType( requestDTO.getNotificationType() );

        return sendNotificationRequest;
    }

    protected RecipientDetails recipientDetailsDTOToRecipientDetails(RecipientDetailsDTO recipientDetailsDTO) {
        if ( recipientDetailsDTO == null ) {
            return null;
        }

        RecipientDetails recipientDetails = new RecipientDetails();

        recipientDetails.setEmail( recipientDetailsDTO.getEmail() );
        recipientDetails.setPhone( recipientDetailsDTO.getPhone() );
        Map<String, String> map = recipientDetailsDTO.getPlaceHolders();
        if ( map != null ) {
            recipientDetails.setPlaceHolders( new HashMap<String, String>( map ) );
        }
        else {
            recipientDetails.setPlaceHolders( null );
        }

        return recipientDetails;
    }
}
