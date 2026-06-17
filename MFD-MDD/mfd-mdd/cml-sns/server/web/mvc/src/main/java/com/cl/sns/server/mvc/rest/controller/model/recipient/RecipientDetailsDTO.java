package com.cl.sns.server.mvc.rest.controller.model.recipient;

import com.cl.sns.server.mvc.rest.controller.model.BaseDTO;

import java.util.Map;

public class RecipientDetailsDTO extends BaseDTO {
    private String email;
    private String phone;
    private Map<String, String> placeHolders;

    public String getEmail() {
        return email;
    }

    public RecipientDetailsDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public RecipientDetailsDTO setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Map<String, String> getPlaceHolders() {
        return placeHolders;
    }

    public RecipientDetailsDTO setPlaceHolders(Map<String, String> placeHolders) {
        this.placeHolders = placeHolders;
        return this;
    }
}
