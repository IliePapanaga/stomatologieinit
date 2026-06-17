package com.cl.mdd.server.mvc.captcha.recaptcha;

import com.cl.mdd.server.mvc.captcha.CaptchaException;
import com.cl.mdd.server.mvc.captcha.CaptchaValidator;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReCaptcha implements CaptchaValidator {

    @Value("${captcha.recaptcha.secret:6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe}")
    // https://developers.google.com/recaptcha/docs/faq
    private String secret;

    @Value("${captcha.recaptcha.site:6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI}")
    // https://developers.google.com/recaptcha/docs/faq
    private String site;

    @Value("${captcha.recaptcha.response.header:g-recaptcha-response}")
    private String reCaptchaHeader;

    private final ReCaptchaVerify reCaptchaVerify;

    public ReCaptcha(ReCaptchaVerify reCaptchaVerify) {
        this.reCaptchaVerify = reCaptchaVerify;
    }

    @Override
    public boolean verify(HttpServletRequest httpServletRequest) {
        String response = httpServletRequest.getHeader(reCaptchaHeader);
        if (response == null) {
            return false;
        }
        if (!reCaptchaVerify.verify(secret, response).isSuccess()) {
            throw new CaptchaException("ReCaptcha unsuccessful", "E_WEB_CAPTCHA_RECAPTCHA_ERROR");
        }
        return true;
    }

    @Override
    public String clientKey() {
        return site;
    }

    @Override
    public void send(HttpServletResponse response) {
        throw new UnsupportedOperationException("Google sends ReCaptcha challenge");
    }
}
