package com.cl.mdd.server.mvc.captcha.recaptcha;

import feign.Param;
import feign.RequestLine;

public interface ReCaptchaVerify {

    @RequestLine("GET /recaptcha/api/siteverify?secret={secret}&response={response}")
    ReCaptchaResponse verify(@Param("secret") String secret, @Param("response") String response);

}
