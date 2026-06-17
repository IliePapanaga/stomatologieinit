package com.cl.mdd.server.mvc.rest.graphql.exception.strategies;

import com.cl.mdd.server.mvc.rest.graphql.exception.MDDGraphQLError;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.ExecutionPath;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * Access denied exception strategy
 * <p/>
 * Process spring's {@link AccessDeniedException}.
 */
@Component
public class AccessDeniedExceptionStrategy extends BaseExceptionProcessingStrategy {

    @Override
    public void process(DataFetcherExceptionHandlerParameters handlerParameters) {
        Throwable exception = handlerParameters.getException();
        AccessDeniedException exc = (AccessDeniedException) exception.getCause().getCause();
        ExecutionPath segment = handlerParameters.getPath().segment(exc.getLocalizedMessage());
        GraphQLError err = new MDDGraphQLError(segment, exc.getLocalizedMessage(), ErrorType.ExecutionAborted);
        handlerParameters.getExecutionContext().addError(err, segment);
    }

}
