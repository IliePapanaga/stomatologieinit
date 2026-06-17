package com.cl.mdd.server.core.data.model.errors;

public class ErrorInfoModel {
    protected String path;
    protected String message;
    protected String errorCode;
    protected String exceptionClass;

    public ErrorInfoModel() {
    }

    public String getPath() {
        return path;
    }

    public ErrorInfoModel setPath(String path) {
        this.path = path;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ErrorInfoModel setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ErrorInfoModel setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public ErrorInfoModel setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
        return this;
    }
}
