package com.cl.sns.server.mvc.rest.controller.model.common;

import com.cl.sns.server.mvc.rest.controller.exception.ErrorInfo;
import com.cl.sns.server.mvc.rest.controller.model.BaseDTO;

import java.util.List;

public class ResponseDTO<T> extends BaseDTO {
    private int status;
    private T data;
    private List<ErrorInfo> errors;

    public ResponseDTO(int status) {
        this.status = status;
    }

    public ResponseDTO() {
    }

    public List<ErrorInfo> getErrors() {
        return errors;
    }

    public ResponseDTO setErrors(List<ErrorInfo> errors) {
        this.errors = errors;
        return this;
    }

    public T getData() {
        return data;
    }

    public ResponseDTO setData(T data) {
        this.data = data;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public ResponseDTO setStatus(int status) {
        this.status = status;
        return this;
    }
}
