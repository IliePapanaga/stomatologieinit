package com.cl.mdd.server.core.manager.converter;

import com.cl.mdd.server.core.data.model.query.Pagination;
import com.cl.mdd.server.core.data.model.query.QOrder;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

/**
 * Query converter.
 * <p/>
 * Performs conversion for query related models.
 */
@Component
public class QueryConverter {

    @Value("${query.pagination.default.page:0}")
    private int pageDefault;
    @Value("${query.pagination.default.per.page:50}")
    private int perPageDefault;
    /**
     * Construct page request from pagination.
     * <p/>
     *
     * @param pagination
     * @param dictionary
     * @return page request
     */
    public Pageable toPageable(Pagination pagination, Map<String, String> dictionary) {
        Integer page = Optional.ofNullable(pagination.getPage()).orElse(pageDefault);
        Integer perPage = Optional.ofNullable(pagination.getPerPage()).orElse(perPageDefault);

        List<QOrder> orders = pagination.getOrders();
        if (isEmpty(orders)) {
            return new PageRequest(page, perPage);
        } else {
            JpaSort unsafe = null;
            for (QOrder order : orders) {
                Sort.Direction direction = Sort.Direction.fromStringOrNull(order.getDirection().toString());
                String property = dictionary.getOrDefault(order.getField(), order.getField());
                unsafe = unsafe == null ? JpaSort.unsafe(direction, property) : unsafe.andUnsafe(direction, property);
            }

            return new PageRequest(page, perPage, unsafe);
        }
    }

    /**
     * Construct page request from pagination.
     * <p/>
     *
     * @param pagination
     * @return page request
     */
    public Pageable toPageable(Pagination pagination) {
        return toPageable(pagination, emptyMap());
    }


//    /**
//     * Converts dto order to spring data order
//     * Translate order fields using <code>dictionary</code>
//     * <p/>
//     * @param order
//     * @param dictionary
//     * @return sort order.
//     */
//    public Sort.Order toOrder(QOrder order, Map<String, String> dictionary) {
//        Sort.NullHandling nullHandling = null;
//        Boolean nullsFirst = order.getNullsFirst();
//        if (nullsFirst != null) {
//            nullHandling = nullsFirst ? Sort.NullHandling.NULLS_FIRST : Sort.NullHandling.NULLS_LAST;
//        }
//        return new Sort.Order(Sort.Direction.fromStringOrNull(order.getDirection().toString()),
//                dictionary.getOrDefault(order.getField(), order.getField())
//                , nullHandling);
//    }

    /**
     * Convert elements mapped by provided mapper to QueryResult
     * @param page page of elements
     * @param mapper Function which map elements type to response type
     * @param <T> type of response
     * @param <R> type of request
     * @return QueryResult
     */
    public <T, R> QueryResult<T> toQueryResult(Page<R> page, Function<R, T> mapper) {
        List<T> result = emptyIfNull(page.getContent()).stream().map(mapper).collect(Collectors.toList());
        Pagination pagination = convertToPagination(page);
        return new QueryResult<> (result, pagination);
    }

    /**
     * Converts a queried page to pagination model
     * <p/>
     * @param pageable
     * @return pagination model
     */
    public Pagination convertToPagination(Page pageable) {
        return new Pagination(pageable.getTotalElements(), pageable.getNumber(), pageable.getSize());
    }
}
