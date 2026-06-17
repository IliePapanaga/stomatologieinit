package com.cl.sns.server.core.service.msg.mail;

import com.cl.sns.server.core.service.msg.content.MessageDetails;

import java.util.List;

public class MailMessageDetails extends MessageDetails {

    private String subject;

    private List<String> to;

    private List<String> cc;

    private List<String> bcc;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }
}
