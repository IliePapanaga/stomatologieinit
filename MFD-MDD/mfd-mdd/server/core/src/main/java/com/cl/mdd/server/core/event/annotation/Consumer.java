package com.cl.mdd.server.core.event.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
/**
 * Indicates that annotated type is {@link com.cl.mdd.server.core.event.Event} consumer.
 */
public @interface Consumer {

}
