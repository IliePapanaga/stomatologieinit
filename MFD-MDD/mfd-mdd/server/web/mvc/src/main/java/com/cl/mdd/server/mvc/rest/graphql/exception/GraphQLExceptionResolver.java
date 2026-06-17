package com.cl.mdd.server.mvc.rest.graphql.exception;

import com.cl.mdd.server.core.exception.MDDException;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * MDD Graph QL custom exception resolver
 * <p />
 */
public class GraphQLExceptionResolver implements DataFetcherExceptionHandler {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<Class<? extends Throwable>, ExceptionProcessingStrategy> strategies;

    private ExceptionProcessingStrategy defaultExceptionProcessingStrategy;

    public GraphQLExceptionResolver(Map<Class<? extends Throwable>, ExceptionProcessingStrategy> strategies, ExceptionProcessingStrategy defaultStrategy) {
        Validate.notNull(strategies);
        Validate.notNull(defaultStrategy);
        this.strategies = strategies;
        this.defaultExceptionProcessingStrategy = defaultStrategy;
    }

    @Override
    public void accept(DataFetcherExceptionHandlerParameters handlerParameters) {
        logger.error("GraphQL error intercepted", handlerParameters.getException());
        Throwable exception = handlerParameters.getException();
        Throwable wrapped = exception.getCause().getCause();

        ExceptionProcessingStrategy strategy = strategies.get(wrapped.getClass());
        if(strategy == null && wrapped instanceof MDDException){
            strategy = strategies.get(MDDException.class);
        }

        Optional.ofNullable(strategy).orElse(defaultExceptionProcessingStrategy).process(handlerParameters);
    }
}
