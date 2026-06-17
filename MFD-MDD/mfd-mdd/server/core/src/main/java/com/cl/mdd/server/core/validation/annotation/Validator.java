package com.cl.mdd.server.core.validation.annotation;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Scope(SCOPE_PROTOTYPE)
/**
 * Indicates that annotated type is {@link javax.validation.ConstraintValidator}.
 */
public @interface Validator {

}
