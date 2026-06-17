package com.cl.mdd.server.core.manager.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
/**
 * Indicates that annotated type is a manager.
 */
public @interface Manager {

}
