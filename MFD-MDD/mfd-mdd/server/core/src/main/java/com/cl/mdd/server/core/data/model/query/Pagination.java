package com.cl.mdd.server.core.data.model.query;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class Pagination {

    private Long total;

    private Integer page;

    private Integer perPage;

    private List<QOrder> orders;

    public Pagination() {
    }

    public Pagination(Integer page, Integer perPage) {
        this(null, page, perPage);
    }

    public Pagination(Long total, Integer page, Integer perPage) {
        this.total = total;
        this.page = page;
        this.perPage = perPage;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public Pagination setPage(Integer page) {
        this.page = page;
        return this;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public Pagination setPerPage(Integer perPage) {
        this.perPage = perPage;
        return this;
    }

    public List<QOrder> getOrders() {
        return orders;
    }

    public Pagination setOrders(List<QOrder> orders) {
        this.orders = orders;
        return this;
    }

    public Pagination withOrders(List<? extends IOrder> orders) {
        List<QOrder> qOrders = CollectionUtils.emptyIfNull(orders).stream()
                .map(order -> new QOrder(order.getPath(), order.getDirection()))
                .collect(Collectors.toList());
        this.setOrders(qOrders);
        return this;
    }
}
