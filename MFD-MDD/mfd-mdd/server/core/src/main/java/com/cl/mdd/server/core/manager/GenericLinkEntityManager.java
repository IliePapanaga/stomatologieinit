package com.cl.mdd.server.core.manager;

import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;

public interface GenericLinkEntityManager<T extends Identifiable, A extends Identifiable, B extends Identifiable> extends GenericEntityManager<T> {

    T save(A a, B b);

}
