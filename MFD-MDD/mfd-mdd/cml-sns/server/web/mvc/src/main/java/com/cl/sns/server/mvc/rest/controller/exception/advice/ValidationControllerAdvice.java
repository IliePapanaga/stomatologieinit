package com.cl.sns.server.mvc.rest.controller.exception.advice;


import com.cl.sns.server.mvc.rest.controller.exception.ErrorInfo;
import com.cl.sns.server.mvc.rest.controller.model.common.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ValidationControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseDTO> handleException(MethodArgumentNotValidException exception) {
        String exceptionClass = exception.getBindingResult().getTarget().getClass().getName();

        List<ErrorInfo> errorInfos = exception.getBindingResult().getAllErrors().stream()
                .map(err -> new ErrorInfo()
                        .setMessage(err.getDefaultMessage())
                        .setExceptionClass(exceptionClass))
                .collect(Collectors.toList());

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ResponseDTO responseDTO = new ResponseDTO(status.value())
                .setErrors(errorInfos);

        return new ResponseEntity(responseDTO, status);
    }

}
