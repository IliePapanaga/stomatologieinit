package com.cl.mdd.server.core.manager;

import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;

public interface GenericSingleEntityManager<T extends Identifiable> extends GenericEntityManager<T> {

    T save(T entity);

}
