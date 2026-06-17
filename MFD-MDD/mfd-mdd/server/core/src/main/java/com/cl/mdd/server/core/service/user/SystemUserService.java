package com.cl.mdd.server.core.service.user;

import com.cl.mdd.server.core.data.model.RegisterSystemUser;
import com.cl.mdd.server.core.data.model.SystemUserModel;
import com.cl.mdd.server.core.data.model.UserActivateDeactivateResult;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.query.FindSystemUsersQuery;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.service.Service;

import javax.validation.Valid;

public interface SystemUserService extends Service {

    SystemUserModel register(@Valid RegisterSystemUser registerSystemUser);

    SystemUserModel get(String id);

    void update(String systemUserId, ContactModel contact);

    QueryResult<SystemUserModel> findAll(FindSystemUsersQuery query);

    UserActivateDeactivateResult activateDeactivateAccount(String id, boolean enabled);
}
