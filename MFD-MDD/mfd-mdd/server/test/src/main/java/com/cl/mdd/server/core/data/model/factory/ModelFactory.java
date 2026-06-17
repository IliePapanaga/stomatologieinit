package com.cl.mdd.server.core.data.model.factory;

import com.cl.mdd.server.core.data.model.MDDModel;

import java.lang.reflect.ParameterizedType;

public interface ModelFactory<T extends MDDModel> {
    T create();
    T fillFields(T model);

    default Class<T> getType() {
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
