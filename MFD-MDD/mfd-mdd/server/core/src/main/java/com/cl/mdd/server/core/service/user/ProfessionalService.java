package com.cl.mdd.server.core.service.user;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.query.FindAllBlackListedLocationDetailsQuery;
import com.cl.mdd.server.core.data.model.query.FindAllBlackListedLocationSummaryQuery;
import com.cl.mdd.server.core.data.model.query.FindSystemUserProfessionals;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.model.query.model.SystemUserProfessionalModel;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.service.Service;

import javax.validation.Valid;

public interface ProfessionalService extends Service {

    ProfessionalModel register(@Valid RegisterProfessional registerProfessional);

    ProfessionalModel get(String id);

    ProfessionalJobPreferenceModel getProfessionalJobPreference(String id);

    ProfessionalProfileModel getProfessionalProfile(String id);

    void updateProfile(String professionalId, @Valid ProfessionalProfileModel professionalProfileModel);

    void updateJobPreferences(String professionalId, @Valid ProfessionalJobPreferenceModel jobPreferenceModel);

    ProfessionalModel update(@Valid ProfessionalModel professionalModel);

    QueryResult<SystemUserProfessionalModel> getSystemUserProfessionals(FindSystemUserProfessionals queryInfo);

    UserActivateDeactivateResult activateDeactivateAccount(String id, boolean enabled);

    void blackList(@Valid BlackListPracticeLocation blackList);

    void unBlackList(@Valid BlackListPracticeLocation blackList);

    QueryResult<BlackListedLocationDetails> fetch(FindAllBlackListedLocationDetailsQuery queryInfo);

    QueryResult<BlackListedLocationSummary> fetch(FindAllBlackListedLocationSummaryQuery queryInfo);
}
