package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.ComplexTemporaryJobPosting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class UpdateToComplexTemporaryJobPostingFactory extends AbstractModelFactory<ComplexTemporaryJobPosting> {

    @Autowired
    private PublishComplexTemporaryJobPostingFactory publishComplexTemporaryJobPostingFactory;
    @Override
    public ComplexTemporaryJobPosting fillFields(ComplexTemporaryJobPosting model) {
        publishComplexTemporaryJobPostingFactory.fillFields(model);
        return model;
    }

}
