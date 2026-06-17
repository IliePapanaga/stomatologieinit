package com.cl.sns.server.mvc.rest.controller.exception.advice;


import com.amazonaws.AmazonServiceException;
import com.cl.sns.server.mvc.rest.controller.exception.ErrorInfo;
import com.cl.sns.server.mvc.rest.controller.model.common.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
public class NotificationControllerAdvice {

    @ExceptionHandler(AmazonServiceException.class)
    public ResponseEntity<ResponseDTO> amazonSimpleEmailServiceException(final AmazonServiceException awsServiceException) {
        return buildResult(awsServiceException.getErrorMessage(), awsServiceException.getErrorCode(), awsServiceException.getClass().getName());
    }

    protected ResponseEntity<ResponseDTO> buildResult(final String errorMessage,
                                                      final String errorCode,
                                                      final String exceptionClass) {
        ErrorInfo errorInfo = new ErrorInfo()
                .setMessage(errorMessage)
                .setExceptionClass(exceptionClass)
                .setErrorCode(errorCode);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ResponseDTO responseDTO = new ResponseDTO(status.value())
                .setErrors(Arrays.asList(errorInfo));

        return new ResponseEntity(responseDTO, status);
    }


}
