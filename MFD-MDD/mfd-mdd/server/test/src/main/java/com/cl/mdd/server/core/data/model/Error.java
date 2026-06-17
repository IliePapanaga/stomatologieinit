package com.cl.mdd.server.core.data.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class Error {
    private String errorType;
    private String message;
    private List<String> path;

    public String getErrorType() {
        return errorType;
    }

    public Error setErrorType(String errorType) {
        this.errorType = errorType;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Error setMessage(String message) {
        this.message = message;
        return this;
    }

    public List<String> getPath() {
        return path;
    }

    public Error setPath(List<String> path) {
        this.path = path;
        return this;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this,
                ToStringStyle.DEFAULT_STYLE, true, true);
    }
}
