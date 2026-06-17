package com.cl.mdd.server.mvc.captcha;

import com.cl.mdd.server.core.exception.MDDException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Captcha")
public class CaptchaException extends MDDException {

    public CaptchaException(String message, String code) {
        super(message, code);
    }
}
