package com.cl.sns.server.core.service.msg.sms;

import com.cl.sns.server.core.service.msg.content.MessageDetails;

/**
 * SMS message details
 * <p />
 *
 */
public class SmsMessageDetails extends MessageDetails {

    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
