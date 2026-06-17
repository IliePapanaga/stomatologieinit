package com.cl.mdd.server.mvc.rest.graphql.advices;

import com.cl.mdd.server.core.data.model.errors.ErrorInfoModel;
import com.cl.mdd.server.core.exception.MDDException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice("com.cl.mdd.server.mvc.rest")
public class RestErrorHandlerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ResponseEntity<Collection<ErrorInfoModel>> handleAccessDenied(AccessDeniedException exception) {
        ErrorInfoModel error = new ErrorInfoModel();
        error.setMessage(exception.getLocalizedMessage());
        error.setErrorCode(HttpStatus.FORBIDDEN.toString());
        error.setExceptionClass(exception.getClass().getSimpleName());
        return new ResponseEntity<>(Collections.singletonList(error), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MDDException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ResponseEntity<Collection<ErrorInfoModel>> handleMDDException(MDDException exception) {
        ErrorInfoModel error = new ErrorInfoModel();
        error.setMessage(exception.getLocalizedMessage());
        error.setErrorCode(exception.getCode());
        error.setExceptionClass(exception.getClass().getSimpleName());
        return new ResponseEntity<>(Collections.singletonList(error), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<ErrorInfoModel> errorInfos = CollectionUtils.emptyIfNull(ex.getAllErrors()).stream().map(error -> new ErrorInfoModel()
                .setPath(((FieldError) error).getField())
                .setErrorCode(error.getCode())
                .setExceptionClass(ex.getObjectName())
                .setMessage(error.getDefaultMessage())
        ).collect(Collectors.toList());
        return new ResponseEntity<>(errorInfos, status);
    }

}
