package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.exception.MDDException;

public class WrongUsername extends MDDException {

    private static final String CODE = "E_USER_WRONG_USERNAME";

    public WrongUsername() {
        super("", CODE);
    }

    public WrongUsername(String message) {
        super(message, CODE);
    }

    public WrongUsername(String message, Throwable cause) {
        super(message, cause, CODE);
    }
}
