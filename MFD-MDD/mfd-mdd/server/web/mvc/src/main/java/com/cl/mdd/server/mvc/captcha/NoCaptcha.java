package com.cl.mdd.server.mvc.captcha;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoCaptcha implements CaptchaValidator {

    @Override
    public boolean verify(HttpServletRequest httpServletRequest) {
        return true;
    }

    @Override
    public String clientKey() {
        return null;
    }

    @Override
    public void send(HttpServletResponse response) {
    }
}
