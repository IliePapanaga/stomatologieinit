package com.cl.mdd.server.mvc.captcha;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class CaptchaContext {

    @Autowired
    private HttpServletRequest httpServletRequest;

    final private CaptchaValidator captchaValidator;

    @Autowired
    public CaptchaContext(CaptchaValidator captchaValidator) {
        this.captchaValidator = captchaValidator;
    }

    private Boolean captchaResult;

    public boolean isCaptchaPassed() {
        if (captchaResult == null) {
            captchaResult = captchaValidator.verify(httpServletRequest);
        }
        return captchaResult;
    }

}
