package com.cl.mdd.server.mvc.rest.graphql.model;

import com.cl.mdd.server.core.data.model.query.QueryResult;

import java.util.List;

/**
 * Wrapper for a collection of models
 * <p/>
 * Contains models and models's metadata.
 * @param <T> - connection type
 */
public class Connection<T> {
    private Long count;
    private List<T> nodes;

    public Connection(List<T> items) {
        this.nodes = items;
    }

    public Connection(List<T> nodes, Long count) {
        this.count = count;
        this.nodes = nodes;
    }

    public static <T> Connection<T> fromQueryResult(QueryResult<T> queryResult) {
        return new Connection<T>(queryResult.getResult(), queryResult.getPagination().getTotal());
    }

    public List<T> getNodes() {
        return nodes;
    }

    public Long getCount() {
        return count;
    }

    public Connection setCount(Long count) {
        this.count = count;
        return this;
    }
}