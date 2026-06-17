package com.cl.mdd.server.mvc.rest.graphql.exception.strategies;

import com.cl.mdd.server.mvc.rest.graphql.exception.ExceptionProcessingStrategy;
import com.cl.mdd.server.mvc.rest.graphql.exception.MDDSanitizedGraphQLError;
import graphql.ExceptionWhileDataFetching;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseExceptionProcessingStrategy implements ExceptionProcessingStrategy {
    @Value("${exception.resolvers.show.stacktrace:false}")
    private boolean showStacktrace;

    public ExceptionWhileDataFetching buildError(ExecutionPath path, Throwable exception, SourceLocation sourceLocation){
        return new MDDSanitizedGraphQLError(path, exception, sourceLocation, showStacktrace);

    }
}
