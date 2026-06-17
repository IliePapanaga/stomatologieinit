package com.cl.mdd.server.mvc.captcha;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CaptchaValidator {

    /**
     * Verify a captcha.
     *
     * @param httpServletRequest request
     * @return <code>true</code> if validation was performed
     */
    boolean verify(HttpServletRequest httpServletRequest);

    /**
     * Captcha client key.
     *
     * @return key
     */
    String clientKey();

    /**
     * Send captcha challenge.
     *
     * @param response servlet response
     */
    void send(HttpServletResponse response);

}
