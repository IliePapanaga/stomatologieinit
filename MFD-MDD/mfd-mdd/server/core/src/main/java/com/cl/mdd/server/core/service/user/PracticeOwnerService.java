package com.cl.mdd.server.core.service.user;

import com.cl.mdd.server.core.data.model.PracticeOwnerModel;
import com.cl.mdd.server.core.data.model.RegisterPracticeOwner;
import com.cl.mdd.server.core.data.model.UserActivateDeactivateResult;
import com.cl.mdd.server.core.service.Service;

import javax.validation.Valid;

public interface PracticeOwnerService extends Service {

    PracticeOwnerModel register(@Valid RegisterPracticeOwner model);

    PracticeOwnerModel get(String id);

    UserActivateDeactivateResult activateDeactivateAccount(String id, boolean enabled);

    PracticeOwnerModel update(@Valid PracticeOwnerModel practiceOwnerModel);

}
