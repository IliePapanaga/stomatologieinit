package com.cl.sns.server.mvc.rest.controller.exception;

/**
 * Error info model
 * <p/>
 */
public class ErrorInfo {

    protected String message;

    protected String errorCode;

    protected String exceptionClass;

    public ErrorInfo() {
    }

    public String getMessage() {
        return message;
    }

    public ErrorInfo setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ErrorInfo setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public ErrorInfo setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
        return this;
    }
}
