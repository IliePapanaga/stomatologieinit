package com.cl.mdd.server.core.event.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
/**
 * Indicates that annotated type is an @{@link com.cl.mdd.server.core.event.bus.EventBus} for {@link com.cl.mdd.server.core.event.Event}s .
 */
public @interface EventBus {

}
