package com.cl.mdd.server.mvc.config;


import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.service.notification.NotificationServiceException;
import com.cl.mdd.server.mvc.rest.graphql.exception.ExceptionProcessingStrategy;
import com.cl.mdd.server.mvc.rest.graphql.exception.GraphQLExceptionResolver;
import com.cl.mdd.server.mvc.rest.graphql.exception.strategies.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * MDD exception resolver configuration
 * <p/>
 */
@Configuration
public class ExceptionResolversConfig {

    @Autowired
    private DefaultExceptionProcessingStrategy defaultExceptionProcessingStrategy;
    @Autowired
    private ValidationConstraintViolationStrategy validationConstraintViolationStrategy;
    @Autowired
    private AccessDeniedExceptionStrategy accessDeniedExceptionStrategy;
    @Autowired
    private MDDExceptionProcessingStrategy mddExceptionProcessingStrategy;
    @Autowired
    private NotificationServiceExceptionStrategy notificationServiceExceptionStrategy;

    @Bean
    public GraphQLExceptionResolver graphQLExceptionResolver() {
        Map<Class<? extends Throwable>, ExceptionProcessingStrategy> strategies = new HashMap<>();
        strategies.put(ConstraintViolationException.class, validationConstraintViolationStrategy);
        strategies.put(AccessDeniedException.class, accessDeniedExceptionStrategy);
        strategies.put(MDDException.class, mddExceptionProcessingStrategy);
        strategies.put(NotificationServiceException.class, notificationServiceExceptionStrategy);

        return new GraphQLExceptionResolver(strategies, defaultExceptionProcessingStrategy);
    }
}
