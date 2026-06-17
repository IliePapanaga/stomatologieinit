package com.cl.mdd.server.core.security.authorization.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
/**
 * Indicates that annotated type is an {@link com.cl.mdd.server.core.security.authorization.AccessAuthorizer}.
 */
public @interface AccessAuthorizer {

}
