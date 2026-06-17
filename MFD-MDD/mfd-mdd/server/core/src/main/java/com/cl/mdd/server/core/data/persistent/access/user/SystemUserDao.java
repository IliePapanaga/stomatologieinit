package com.cl.mdd.server.core.data.persistent.access.user;

import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.user.SystemUser;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemUserDao extends AbstractDao<SystemUser> {

}
