package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.MDDModel;
import com.cl.mdd.server.core.data.model.factory.ModelFactory;
import com.cl.mdd.server.core.exception.MDDException;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractModelFactory<T extends MDDModel> implements ModelFactory<T> {
    @Autowired
    protected ContactModelFactory contactModelFactory;

    @Autowired
    protected MddRandomUtils mddRandomUtils;

    public T create(){
        T model = newInstance();
        return fillFields(model);
    }

    public T newInstance(){
        Class<T> type = getType();

        try {
           return type.newInstance();
        } catch (Exception e) {
            throw new MDDException("Cannot instantiate class : " + type.getSimpleName(), e, "CANNOT_INSTANTIATE");
        }
    }

}
