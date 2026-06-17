package com.cl.sns.server.core.service.msg.content;

import com.cl.sns.server.core.model.api.BaseModel;

/**
 * Message details
 * <p />
 */
public abstract class MessageDetails extends BaseModel {

    private String from;

    private String body;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
