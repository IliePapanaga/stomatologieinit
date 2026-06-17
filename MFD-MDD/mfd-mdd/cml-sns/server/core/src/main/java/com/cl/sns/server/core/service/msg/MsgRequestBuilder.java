package com.cl.sns.server.core.service.msg;

import com.cl.sns.server.core.service.msg.content.MessageDetails;

public interface MsgRequestBuilder<T> {
    <T> T build(MessageDetails messageDetails) ;
}
