package com.cl.sns.server.core.service.msg;

import com.cl.sns.server.core.service.msg.content.MessageDetails;

public interface Messenger {

    /**
     * Send notification based on {@link MessageDetails}
     * @param messageDetails
     */
    void send(MessageDetails messageDetails);

}
