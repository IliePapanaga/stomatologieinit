package com.cl.mdd.server.core.security.token;

/**
 * Verifies token through {@link #verify(String)}
 */
public interface TokenVerifier<T> {

    T verify(String token);

}
