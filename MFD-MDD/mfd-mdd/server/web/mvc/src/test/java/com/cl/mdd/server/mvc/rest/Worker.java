package com.cl.mdd.server.mvc.rest;

import com.cl.mdd.server.core.data.model.factory.ModelFactoryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public abstract class Worker {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private ModelFactoryRegistry modelFactoryRegistry;

    public <T> T create(Class<T> type) {
        return modelFactoryRegistry.create(type);
    }
}
