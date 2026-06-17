package com.cl.mdd.server.core.service.posting;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.query.*;
import com.cl.mdd.server.core.service.Service;

import javax.validation.Valid;

public interface JobPostingService extends Service {

    JobPosting get(String id);

    String publish(@Valid PublishSimpleTemporaryJobPosting simpleTemporaryJobPosting);

    String publish(@Valid PublishWeeklyTemporaryJobPosting weeklyTemporaryJobPosting);

    String publish(@Valid PublishComplexTemporaryJobPosting complexTemporaryJobPosting);

    String publish(@Valid PublishSimplePermanentJobPosting simplePermanentJobPosting);

    void update(@Valid SimpleTemporaryJobPosting simpleTemporaryJobPosting);

    void update(@Valid WeeklyTemporaryJobPosting weeklyTemporaryJobPosting);

    void update(@Valid ComplexTemporaryJobPosting complexTemporaryJobPosting);

    void update(@Valid SimplePermanentJobPosting simplePermanentJobPosting);

    void cancel(String id);

    void delete(String id);

    QueryResult<ProfessionalTemporaryJobPosting> fetch(ProfessionalTemporaryJobPostingQuery queryInfo);

    QueryResult<ProfessionalPermanentJobPosting> fetch(ProfessionalPermanentJobPostingQuery queryInfo);

    QueryResult<PracticeOwnerTemporaryJobPosting> fetch(PracticeOwnerTemporaryJobPostingQuery queryInfo);

    QueryResult<PracticeOwnerPermanentJobPosting> fetch(PracticeOwnerPermanentJobPostingQuery queryInfo);

    QueryResult<SystemUserTemporaryJobPosting> fetch(SystemUserTemporaryJobPostingQuery queryInfo);

    QueryResult<SystemUserPermanentJobPosting> fetch(SystemUserPermanentJobPostingQuery queryInfo);

}