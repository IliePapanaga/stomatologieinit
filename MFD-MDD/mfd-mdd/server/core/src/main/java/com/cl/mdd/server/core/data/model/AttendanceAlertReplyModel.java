package com.cl.mdd.server.core.data.model;

import java.time.ZonedDateTime;

public class AttendanceAlertReplyModel {

    private String template;

    private ZonedDateTime replyDate;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public ZonedDateTime getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(ZonedDateTime replyDate) {
        this.replyDate = replyDate;
    }
}
