package com.cl.mdd.server.mvc.captcha.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize("@captchaContext.isCaptchaPassed()")
public @interface RequiresCaptcha {

}