package com.cl.sns.server.core.model.api.notification;

import com.cl.sns.server.core.model.api.BaseModel;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class RecipientDetails extends BaseModel {

    @NotNull(message = "{send.notification.email.not.null}")
    private String email;

    @NotNull(message = "{send.notification.phone.not.null}")
    private String phone;

    private Map<String, String> placeHolders;

    public String getEmail() {
        return email;
    }

    public RecipientDetails setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public RecipientDetails setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Map<String, String> getPlaceHolders() {
        return placeHolders;
    }

    public RecipientDetails setPlaceHolders(Map<String, String> placeHolders) {
        this.placeHolders = placeHolders;
        return this;
    }
}
