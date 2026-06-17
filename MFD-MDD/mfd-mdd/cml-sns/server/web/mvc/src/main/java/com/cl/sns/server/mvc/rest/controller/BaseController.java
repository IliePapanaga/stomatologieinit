package com.cl.sns.server.mvc.rest.controller;

import com.cl.sns.server.mvc.rest.controller.model.common.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public abstract class BaseController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public ResponseEntity<ResponseDTO> wrapResult(Object data, HttpStatus status) {
        if(status == null){
            status = HttpStatus.OK;
        }
        ResponseDTO responseDTO = new ResponseDTO(status.value())
                .setData(data);

        return new ResponseEntity(responseDTO, status);
    }

    public ResponseEntity<ResponseDTO> wrapResult(Object data) {
       return wrapResult(data, null);
    }

    public ResponseEntity<ResponseDTO> wrapResult() {
       return wrapResult(null, null);
    }

}