package com.cl.sns.server.mvc.rest.controller.model.notification;

import com.cl.sns.server.mvc.rest.controller.model.BaseDTO;
import com.cl.sns.server.mvc.rest.controller.model.recipient.RecipientDetailsDTO;

public class SendNotificationRequestDTO extends BaseDTO {

    private String notificationType;

    private RecipientDetailsDTO recipientDetails;

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public RecipientDetailsDTO getRecipientDetails() {
        return recipientDetails;
    }

    public void setRecipientDetails(RecipientDetailsDTO recipientDetails) {
        this.recipientDetails = recipientDetails;
    }
}
