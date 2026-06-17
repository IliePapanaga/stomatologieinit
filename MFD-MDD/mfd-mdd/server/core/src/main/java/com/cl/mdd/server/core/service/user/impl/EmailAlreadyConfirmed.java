package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.exception.MDDException;

public class EmailAlreadyConfirmed extends MDDException {

    public static final String CODE = "E_USER_EMAIL_ALREADY_CONFIRMED";

    public EmailAlreadyConfirmed() {
        super("", CODE);
    }

    public EmailAlreadyConfirmed(String message) {
        super(message, CODE);
    }

    public EmailAlreadyConfirmed(String message, Throwable cause) {
        super(message, cause, CODE);
    }
}
