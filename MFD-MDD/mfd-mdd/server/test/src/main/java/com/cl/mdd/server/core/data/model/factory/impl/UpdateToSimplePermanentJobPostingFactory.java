package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.SimplePermanentJobPosting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class UpdateToSimplePermanentJobPostingFactory extends AbstractModelFactory<SimplePermanentJobPosting> {

    @Autowired
    private PublishSimplePermanentJobPostingFactory publishSimplePermanentJobPostingFactory;

    @Override
    public SimplePermanentJobPosting fillFields(SimplePermanentJobPosting model) {
        publishSimplePermanentJobPostingFactory.fillFields(model);
        return model;
    }

}
