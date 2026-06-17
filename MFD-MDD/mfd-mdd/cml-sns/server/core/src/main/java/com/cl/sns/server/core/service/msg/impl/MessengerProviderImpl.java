package com.cl.sns.server.core.service.msg.impl;

import com.cl.sns.server.core.service.msg.Messenger;
import com.cl.sns.server.core.service.msg.MessengerProvider;
import org.apache.commons.lang3.Validate;

import java.util.Map;

/**
 * Default Message provider
 * <p />
 *
 */
public class MessengerProviderImpl implements MessengerProvider{

    private Map<String, Messenger> messengerRegistry;

    public MessengerProviderImpl(Map<String, Messenger> messengerRegistry){
        Validate.notNull(messengerRegistry);
        this.messengerRegistry=messengerRegistry;
    }

    /**
     * Provides messenger by key
     * @param messengerKey
     * @return the corresponding messenger
     * @throws {@link IllegalArgumentException}
     */
    @Override
    public Messenger lookUp(String messengerKey) {
        Messenger messenger = messengerRegistry.get(messengerKey);
        if(messenger == null){
            throw new IllegalArgumentException("Unsupported message key :" + messengerKey);
        }
        return messenger;
    }
}
