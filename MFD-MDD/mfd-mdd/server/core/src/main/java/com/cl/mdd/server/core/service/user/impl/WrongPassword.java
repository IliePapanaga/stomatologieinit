package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.exception.MDDException;

public class WrongPassword extends MDDException {

    private static final String CODE = "E_USER_WRONG_PASSWORD";

    public WrongPassword() {
        super("", CODE);
    }

    public WrongPassword(String message) {
        super(message, CODE);
    }

    public WrongPassword(String message, Throwable cause) {
        super(message, cause, CODE);
    }
}
