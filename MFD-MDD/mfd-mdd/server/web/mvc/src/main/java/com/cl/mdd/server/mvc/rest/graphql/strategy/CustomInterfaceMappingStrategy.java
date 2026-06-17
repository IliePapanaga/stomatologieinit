package com.cl.mdd.server.mvc.rest.graphql.strategy;

import io.leangen.graphql.generator.mapping.strategy.AnnotatedInterfaceStrategy;
import io.leangen.graphql.util.ClassUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

/**
 * Custom interface mapping strategy
 * </p>
 * Register all models from the given package.
 */
public class CustomInterfaceMappingStrategy extends AnnotatedInterfaceStrategy {
    private String basePackage;

    public CustomInterfaceMappingStrategy(boolean mapClasses, String basePackage) {
        super(mapClasses);
        Validate.notNull(basePackage);
        this.basePackage = basePackage;
    }

    @Override
    public boolean supportsInterface(AnnotatedType interfacce) {
        Type type = interfacce.getType();
        return (type.getTypeName().startsWith(basePackage) && ClassUtils.getRawType(type).isInterface())
                || super.supportsInterface(interfacce);
    }
}

