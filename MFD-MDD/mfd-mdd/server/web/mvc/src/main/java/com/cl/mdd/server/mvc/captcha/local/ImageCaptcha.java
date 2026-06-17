package com.cl.mdd.server.mvc.captcha.local;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.servlet.CaptchaServletUtil;
import cn.apiclub.captcha.text.producer.TextProducer;
import com.cl.mdd.server.mvc.captcha.CaptchaException;
import com.cl.mdd.server.mvc.captcha.CaptchaValidator;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;

public class ImageCaptcha implements CaptchaValidator {

    @Value("${captcha.local.parameter:captcha-response}")
    private String responseParameter;

    @Value("${captcha.local.header:CAPTCHA_RESPONSE}")
    private String headerName;

    @Value("${captcha.local.cookie:CAPTCHA_RESPONSE}")
    private String cookieName;

    @Value("${captcha.local.cookie.age:300}")
    private int cookieAge;

    @Value("${captcha.local.width:150}")
    private int width;

    @Value("${captcha.local.height:60}")
    private int height;

    private final PasswordEncoder captchaResponseEncoder;

    private final TextProducer textProducer;

    public ImageCaptcha(PasswordEncoder captchaResponseEncoder, TextProducer textProducer) {
        this.captchaResponseEncoder = captchaResponseEncoder;
        this.textProducer = textProducer;
    }

    @Override
    public boolean verify(HttpServletRequest httpServletRequest) {
        //TODO THINK IF WE WANT TO USE HEADER INSTEAD OF PARAMETER.
        String response = httpServletRequest.getParameter(responseParameter);
        if (response != null) {
            String answer = read(httpServletRequest);
            if (answer == null) {
                throw new CaptchaException("Image Captcha invalid", "E_WEB_CAPTCHA_LOCAL_INCOMPLETE");
            }
            if (!captchaResponseEncoder.matches(response, answer)) {
                throw new CaptchaException("Image Captcha unsuccessful", "E_WEB_CAPTCHA_LOCAL_ERROR");
            }
            return true;
        }
        return false;
    }

    String read(HttpServletRequest request) {
        return Stream.of(ArrayUtils.nullToEmpty(request.getCookies(), Cookie[].class))
                .filter(c -> c.getName().equals(cookieName)).findAny()
                .map(Cookie::getValue)
                .orElseGet(() -> request.getHeader(headerName));
    }

    @Override
    public String clientKey() {
        throw new UnsupportedOperationException("Image Captcha has no site key");
    }

    Captcha build() {
        return new Captcha.Builder(width, height).addText(textProducer).build();
    }

    void saveAnswer(Captcha captcha, HttpServletResponse response) {
        String answer = captchaResponseEncoder.encode(captcha.getAnswer());
        Cookie cookie = new Cookie(cookieName, answer);
        cookie.setMaxAge(cookieAge); // instead of using session timeout
        // Could not find any restrictions on using relative cookie path, but it looks like they exist.
        cookie.setPath("/api/"); // this binds the app to root context. However, this also seems to be the case for MDD.
        response.addCookie(cookie);
        response.addHeader(headerName, answer);
    }

    void write(Captcha captcha, HttpServletResponse response) {
        try {
            CaptchaServletUtil.writeImage(response.getOutputStream(), captcha.getImage());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void send(HttpServletResponse response) {
        Captcha captcha = build();
        saveAnswer(captcha, response);
        write(captcha, response);
    }


}
