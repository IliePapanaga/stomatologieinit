package com.cl.mdd.server.core.data.model.query;

import java.util.List;

public class QueryResult<T> {

    private List<T> result;

    private Pagination pagination;

    public QueryResult() {
    }

    public QueryResult(List<T> result) {
        this.result = result;
    }

    public QueryResult(List<T> result, Pagination pagination) {
        this(result);
        this.pagination = pagination;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
