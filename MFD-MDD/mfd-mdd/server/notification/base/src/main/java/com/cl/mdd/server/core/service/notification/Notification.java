package com.cl.mdd.server.core.service.notification;

import java.util.Map;

public class Notification {

    private String phone;

    private String email;

    private String type;

    private Map<String, String> context;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }

    public Map<String, String> getContext() {
        return context;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", type='" + type + '\'' +
                ", context=" + context +
                '}';
    }
}
