package com.cl.sns.server.core.model.api.notification;

import com.cl.sns.server.core.model.api.BaseModel;

import javax.validation.constraints.NotNull;

public class SendNotificationRequest extends BaseModel {

    @NotNull(message = "{send.notification.type.not.null}")
    private String notificationType;

    @NotNull(message = "{send.notification.recipient.not.null}")
    private RecipientDetails recipientDetails;

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public RecipientDetails getRecipientDetails() {
        return recipientDetails;
    }

    public void setRecipientDetails(RecipientDetails recipientDetails) {
        this.recipientDetails = recipientDetails;
    }
}
