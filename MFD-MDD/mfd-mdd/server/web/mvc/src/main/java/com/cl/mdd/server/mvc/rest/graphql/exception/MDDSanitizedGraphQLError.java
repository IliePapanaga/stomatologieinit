package com.cl.mdd.server.mvc.rest.graphql.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import graphql.ExceptionWhileDataFetching;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class MDDSanitizedGraphQLError extends ExceptionWhileDataFetching {

    private boolean showStackTrace;

    public MDDSanitizedGraphQLError(ExecutionPath path, Throwable exception, SourceLocation sourceLocation, boolean showStackTrace) {
        super(path, exception, sourceLocation);
        this.showStackTrace = showStackTrace;
    }

    @Override
    @JsonIgnore
    public Throwable getException() {
        return super.getException();
    }

    public String getStackTrace() {
        if(showStackTrace){
            return ExceptionUtils.getStackTrace(getException());
        }

        return StringUtils.EMPTY;
    }

}
