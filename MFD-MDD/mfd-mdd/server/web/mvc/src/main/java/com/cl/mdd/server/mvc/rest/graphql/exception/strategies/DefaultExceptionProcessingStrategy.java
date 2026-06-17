package com.cl.mdd.server.mvc.rest.graphql.exception.strategies;

import graphql.ExceptionWhileDataFetching;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;
import org.springframework.stereotype.Component;

/**
 * Default MDD exception processing strategy.
 * <p/>
 */
@Component
public class DefaultExceptionProcessingStrategy extends BaseExceptionProcessingStrategy {

    @Override
    public void process(DataFetcherExceptionHandlerParameters handlerParameters) {
        Throwable exception = handlerParameters.getException();
        SourceLocation sourceLocation = handlerParameters.getField().getSourceLocation();
        ExecutionPath path = handlerParameters.getPath();

        ExceptionWhileDataFetching error = buildError(path, exception, sourceLocation);
        handlerParameters.getExecutionContext().addError(error, handlerParameters.getPath());
    }

}
