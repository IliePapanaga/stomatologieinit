package com.cl.mdd.server.core.manager.user;

import com.cl.mdd.server.core.data.model.SystemUserModel;
import com.cl.mdd.server.core.data.model.query.QueryInfo;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.persistent.model.user.SystemUser;
import com.cl.mdd.server.core.manager.GenericSingleEntityManager;

public interface SystemUserManager extends GenericSingleEntityManager<SystemUser> {

    SystemUser get(String id);

    QueryResult<SystemUserModel> findAll(QueryInfo queryInfo);
}
