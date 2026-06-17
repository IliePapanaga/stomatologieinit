package com.cl.sns.server.core.service.msg;


/**
 * Messenger provider.
 * <p/>
 */
public interface MessengerProvider {
    /**
     * Provides messenger by key
     * <p/>
     *
     * @param messengerKey
     */
    Messenger lookUp(String messengerKey);

}
