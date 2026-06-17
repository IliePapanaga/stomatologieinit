package com.cl.mdd.server.mvc.config;

import cn.apiclub.captcha.text.producer.DefaultTextProducer;
import cn.apiclub.captcha.text.producer.TextProducer;
import com.cl.mdd.server.mvc.captcha.CaptchaContext;
import com.cl.mdd.server.mvc.captcha.CaptchaValidator;
import com.cl.mdd.server.mvc.captcha.NoCaptcha;
import com.cl.mdd.server.mvc.captcha.TwoCaptchas;
import com.cl.mdd.server.mvc.captcha.local.ImageCaptcha;
import com.cl.mdd.server.mvc.captcha.recaptcha.ReCaptcha;
import com.cl.mdd.server.mvc.captcha.recaptcha.ReCaptchaVerify;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class CaptchaConfig {

    private static final String CAPTCHA_TEXT_DEV = "devcap";

    @Value("${captcha.enable:true}")
    private boolean captchaEnabled;

    @Value("${captcha.local.enable:true}")
    private boolean localCaptchaEnabled;

    @Value("${captcha.recaptcha.host:https://www.google.com}")
    private String reCaptchaHostname;

    @Bean
    public ReCaptchaVerify reCaptchaVerify() {
        return Feign.builder()
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(ReCaptchaVerify.class))
                .target(ReCaptchaVerify.class, reCaptchaHostname);
    }

    @Bean
    @Autowired
    public ReCaptcha reCaptcha(ReCaptchaVerify reCaptchaVerify) {
        return new ReCaptcha(reCaptchaVerify);
    }

    @Bean
    @Profile("dev")
    public TextProducer devTextProducer() {
        return () -> CAPTCHA_TEXT_DEV;
    }

    @Bean
    @Profile("!dev")
    public TextProducer textProducer() {
        return new DefaultTextProducer();
    }

    @Bean
    @Autowired
    public CaptchaValidator localCaptcha(PasswordEncoder captchaResponseEncoder, TextProducer textProducer) {
        return localCaptchaEnabled ? new ImageCaptcha(captchaResponseEncoder, textProducer) : new NoCaptcha();
    }

    @Bean
    @Autowired
    public CaptchaValidator captchaValidator(CaptchaValidator reCaptcha, CaptchaValidator localCaptcha) {
        return captchaEnabled ? new TwoCaptchas(reCaptcha, localCaptcha) : new NoCaptcha();
    }

    @Bean
    @Autowired
    @RequestScope
    public CaptchaContext captchaContext(CaptchaValidator captchaValidator) {
        return new CaptchaContext(captchaValidator);
    }

}
