package com.cl.mdd.server.core.service.practice;

import com.cl.mdd.server.core.data.model.BlackListProfessional;
import com.cl.mdd.server.core.data.model.BlackListedProfessionalDetails;
import com.cl.mdd.server.core.data.model.PracticeModel;
import com.cl.mdd.server.core.data.model.RegisterPractice;
import com.cl.mdd.server.core.data.model.common.SpecialityModel;
import com.cl.mdd.server.core.data.model.query.FindAllBlackListedProfessionalDetailsQuery;
import com.cl.mdd.server.core.data.model.query.FindSystemUserPractices;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.model.query.model.SystemUserPracticeModel;

import javax.validation.Valid;
import java.util.List;

public interface PracticeService {

    PracticeModel register(RegisterPractice registerPractice, String ownerId);

    PracticeModel getById(String id);

    PracticeModel getByOwnerUsername(String id);

    List<SpecialityModel> getPracticeSpecialities(PracticeModel practiceModel);

    QueryResult<SystemUserPracticeModel> getSystemUserPractices(FindSystemUserPractices queryInfo);

    void update(PracticeModel updatePractice);

    void blackList(@Valid BlackListProfessional blackList);

    void unBlackList(@Valid BlackListProfessional blackList);

    QueryResult<BlackListedProfessionalDetails> fetch(FindAllBlackListedProfessionalDetailsQuery query);
}
