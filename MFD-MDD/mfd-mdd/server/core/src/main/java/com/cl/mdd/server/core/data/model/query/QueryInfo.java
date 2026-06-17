package com.cl.mdd.server.core.data.model.query;

/**
 * Query info model.
 * <p/>
 * Query info without parameters declarations model
 */
public class QueryInfo {

    private Pagination pagination;

    public QueryInfo() {
        this.pagination = new Pagination();
    }

    public QueryInfo(Integer page, Integer perPage) {
        this();
        pagination.setPage(page);
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
