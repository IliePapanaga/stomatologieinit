package com.cl.mdd.server.mvc.rest.graphql.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorHelper;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.Map;

import static graphql.Assert.assertNotNull;

public class MDDGraphQLError implements GraphQLError {
    private ErrorType errorType;
    private final String message;
    private final List<Object> path;

    public MDDGraphQLError(ExecutionPath path, String message,  ErrorType errorType) {
        this.path = assertNotNull(path).toList();
        this.message = assertNotNull(message);
        this.errorType = assertNotNull(errorType);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    @JsonIgnore
    public List<SourceLocation> getLocations() {
        return null;
    }

    public List<Object> getPath() {
        return path;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return null;
    }

    @Override
    public ErrorType getErrorType() {
        return this.errorType;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this,
                ToStringStyle.DEFAULT_STYLE, true, true);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        return GraphqlErrorHelper.equals(this, o);
    }

    @Override
    public int hashCode() {
        return GraphqlErrorHelper.hashCode(this);
    }

}
