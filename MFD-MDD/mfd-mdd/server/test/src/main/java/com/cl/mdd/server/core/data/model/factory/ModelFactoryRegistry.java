package com.cl.mdd.server.core.data.model.factory;

import com.cl.mdd.server.core.data.model.factory.impl.AbstractModelFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ModelFactoryRegistry {
    @Autowired
    private List<AbstractModelFactory> factories;
    private Map<Class, ModelFactory> registry = new HashMap<>();

    @PostConstruct
    public void init() {
        CollectionUtils.emptyIfNull(factories)
                .forEach(factory -> registry.put(factory.getType(), factory));
    }

    public <T> T create(Class<T> type){
        ModelFactory factory = registry.get(type);
        Validate.notNull(factory,"Unsupported factory type : " + type.getSimpleName());

        return (T) factory.create();

    }




}
