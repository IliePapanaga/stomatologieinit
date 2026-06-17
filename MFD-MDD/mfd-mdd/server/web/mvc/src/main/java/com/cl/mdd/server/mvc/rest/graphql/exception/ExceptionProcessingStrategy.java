package com.cl.mdd.server.mvc.rest.graphql.exception;


import graphql.execution.DataFetcherExceptionHandlerParameters;

/**
 * Exception processing strategy interface
 * <p/>
 */
public interface ExceptionProcessingStrategy {

    void process(DataFetcherExceptionHandlerParameters handlerParameters);

}
