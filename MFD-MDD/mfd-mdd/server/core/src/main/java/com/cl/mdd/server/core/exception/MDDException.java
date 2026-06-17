package com.cl.mdd.server.core.exception;

public class MDDException extends RuntimeException {

    private String code;

    public MDDException(String message, String code) {
        super(message);
        this.code = code;
    }

    public MDDException(String message, Throwable cause, String code) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
