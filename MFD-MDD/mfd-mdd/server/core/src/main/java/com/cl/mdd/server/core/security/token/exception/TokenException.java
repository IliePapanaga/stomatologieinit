package com.cl.mdd.server.core.security.token.exception;


import com.cl.mdd.server.core.exception.MDDException;

public abstract class TokenException extends MDDException {

    TokenException(String message, String code) {
        super(message, code);
    }
}
