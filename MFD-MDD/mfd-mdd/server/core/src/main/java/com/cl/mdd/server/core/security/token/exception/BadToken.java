package com.cl.mdd.server.core.security.token.exception;

public class BadToken extends TokenException {

    public BadToken(String message, String code) {
        super(message, code);
    }
}
