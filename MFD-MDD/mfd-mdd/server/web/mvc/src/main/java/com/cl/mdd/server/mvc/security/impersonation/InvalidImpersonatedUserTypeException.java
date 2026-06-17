package com.cl.mdd.server.mvc.security.impersonation;

import org.springframework.security.core.AuthenticationException;

public class InvalidImpersonatedUserTypeException extends AuthenticationException {

    public InvalidImpersonatedUserTypeException(String msg) {
        super(msg);
    }

    public InvalidImpersonatedUserTypeException(String msg, Throwable t) {
        super(msg, t);
    }
}
