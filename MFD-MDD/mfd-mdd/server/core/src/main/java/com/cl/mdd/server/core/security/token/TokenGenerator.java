package com.cl.mdd.server.core.security.token;

/**
 * Generates token through {@link #generate(T)}
 */
public interface TokenGenerator<T> {

    String generate(T object);

}
