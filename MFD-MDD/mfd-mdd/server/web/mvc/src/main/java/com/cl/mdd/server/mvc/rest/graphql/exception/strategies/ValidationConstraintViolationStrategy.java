package com.cl.mdd.server.mvc.rest.graphql.exception.strategies;

import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.mvc.rest.graphql.exception.MDDGraphQLError;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.ExecutionPath;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;

/**
 * Validation constraint violation exception strategy
 * <p/>
 * Process {@link ConstraintViolationException} and register violations as errors in graph ql execution result
 */
@Component
public class ValidationConstraintViolationStrategy extends BaseExceptionProcessingStrategy {

    @Override
    public void process(DataFetcherExceptionHandlerParameters handlerParameters) {
        Throwable exception = handlerParameters.getException();
        ConstraintViolationException exc = (ConstraintViolationException) exception.getCause().getCause();
        ExecutionPath path = handlerParameters.getPath();

        CollectionUtils.emptyIfNull(exc.getConstraintViolations()).stream()
                .forEach(violation ->{
                    MDDException ex = new MDDException(violation.getMessage(), violation.getMessageTemplate());
                    ExecutionPath segment = path.segment(violation.getPropertyPath().toString());
                    GraphQLError err = new MDDGraphQLError(segment, ex.getLocalizedMessage(), ErrorType.ValidationError);
                    handlerParameters.getExecutionContext().addError(err, segment);
                });

    }

}
