package com.cl.sns.server.core.service.msg.content;

import org.apache.commons.lang3.Validate;

import java.util.Map;

/**
 * Default Message details provider
 * <p/>
 */
public class MessageDetailsProviderImpl implements MessageFactoryProvider {

    private Map<String, MessageDetailsFactory> registry;

    public MessageDetailsProviderImpl(Map<String, MessageDetailsFactory> registry) {
        Validate.notNull(registry);
        this.registry = registry;
    }

    /**
     * Provides message factory by key
     *
     * @return the corresponding message factory
     */
    @Override
    public MessageDetailsFactory lookUp(String key) {
        MessageDetailsFactory factory = registry.get(key);
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported key :" + key);
        }

        return factory;
    }
}
