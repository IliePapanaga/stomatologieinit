package com.cl.sns.server.core.service.common;

public class SnsApplicationException extends RuntimeException {

    private String code;

    public SnsApplicationException(String message, String code) {
        super(message);
        this.code = code;
    }

    public SnsApplicationException(String message, Throwable cause, String code) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
