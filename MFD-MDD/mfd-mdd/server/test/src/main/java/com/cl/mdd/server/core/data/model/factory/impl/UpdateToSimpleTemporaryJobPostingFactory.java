package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.SimpleTemporaryJobPosting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class UpdateToSimpleTemporaryJobPostingFactory extends AbstractModelFactory<SimpleTemporaryJobPosting> {

    @Autowired
    private PublishSimpleTemporaryJobPostingFactory publishSimpleTemporaryJobPostingFactory;

    @Override
    public SimpleTemporaryJobPosting fillFields(SimpleTemporaryJobPosting model) {
        publishSimpleTemporaryJobPostingFactory.fillFields(model);
        return model;
    }

}
