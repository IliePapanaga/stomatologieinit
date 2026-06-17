package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.WeeklyTemporaryJobPosting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class UpdateToWeeklyTemporaryJobPostingFactory extends AbstractModelFactory<WeeklyTemporaryJobPosting> {

    @Autowired
    private PublishWeeklyTemporaryJobPostingFactory publishWeeklyTemporaryJobPostingFactory;

    @Override
    public WeeklyTemporaryJobPosting fillFields(WeeklyTemporaryJobPosting model) {
        publishWeeklyTemporaryJobPostingFactory.fillFields(model);
        return model;
    }

}
