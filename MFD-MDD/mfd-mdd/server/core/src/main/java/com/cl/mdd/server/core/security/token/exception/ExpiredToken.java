package com.cl.mdd.server.core.security.token.exception;

public class ExpiredToken extends TokenException {

    public ExpiredToken(String message, String code) {
        super(message, code);
    }
}
