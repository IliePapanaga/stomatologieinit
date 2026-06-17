package com.cl.sns.server.core.service.msg.content;

/**
 * Message factory provider
 * <p />
 */
public interface MessageFactoryProvider {

    /**
     * Provides message factory by key
     * <p />
     * @return the corresponding message factory
     * @throws {@link IllegalArgumentException} if there is no factory for the given key
     */
    MessageDetailsFactory lookUp(String key);

}
