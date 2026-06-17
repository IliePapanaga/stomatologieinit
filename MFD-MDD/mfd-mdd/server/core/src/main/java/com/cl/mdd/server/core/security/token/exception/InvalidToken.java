package com.cl.mdd.server.core.security.token.exception;

public class InvalidToken extends TokenException {

    public InvalidToken(String message, String code) {
        super(message, code);
    }
}
