package com.cl.mdd.server.mvc.captcha;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TwoCaptchas implements CaptchaValidator {

    private final CaptchaValidator reCaptcha;

    private final CaptchaValidator imageCaptcha;

    public TwoCaptchas(CaptchaValidator reCaptcha, CaptchaValidator imageCaptcha) {
        this.reCaptcha = reCaptcha;
        this.imageCaptcha = imageCaptcha;
    }

    @Override
    public boolean verify(HttpServletRequest httpServletRequest) {
//        if(reCaptcha.verify(httpServletRequest) || imageCaptcha.verify(httpServletRequest)) {
//            return true;
//        }
//        throw new CaptchaException("Captcha unsuccessful", "E_WEB_CAPTCHA_ERROR_NONE");
        return reCaptcha.verify(httpServletRequest) || imageCaptcha.verify(httpServletRequest);
    }

    @Override
    public String clientKey() {
        return reCaptcha.clientKey();
    }

    @Override
    public void send(HttpServletResponse response) {
        imageCaptcha.send(response);
    }
}
