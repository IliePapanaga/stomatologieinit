package com.cl.mdd.server.mvc.rest.graphql.exception.strategies;

import com.cl.mdd.server.core.service.notification.NotificationServiceException;
import com.cl.mdd.server.mvc.rest.graphql.exception.MDDGraphQLError;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.ExecutionPath;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceExceptionStrategy extends BaseExceptionProcessingStrategy {

    @Override
    public void process(DataFetcherExceptionHandlerParameters handlerParameters) {
        Throwable exception = handlerParameters.getException();
        NotificationServiceException exc = (NotificationServiceException) exception.getCause().getCause();
        ExecutionPath path = handlerParameters.getPath();

        GraphQLError err = new MDDGraphQLError(path, exc.getLocalizedMessage(), ErrorType.ExecutionAborted);
        handlerParameters.getExecutionContext().addError(err, path);
    }

}
