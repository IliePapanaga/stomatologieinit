package com.cl.mdd.server.mvc.rest.graphql.exception.strategies;

import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.mvc.rest.graphql.exception.MDDGraphQLError;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.ExecutionPath;
import org.springframework.stereotype.Component;

/**
 * MDD exception processing strategy.
 * <p/>
 */
@Component
public class MDDExceptionProcessingStrategy extends BaseExceptionProcessingStrategy {

    @Override
    public void process(DataFetcherExceptionHandlerParameters handlerParameters) {
        Throwable exception = handlerParameters.getException();
        MDDException exc = (MDDException) exception.getCause().getCause();
        ExecutionPath path = handlerParameters.getPath();

        GraphQLError err = new MDDGraphQLError(path, exc.getLocalizedMessage(), ErrorType.ExecutionAborted);
        handlerParameters.getExecutionContext().addError(err, path);
    }

}
