package com.cl.mdd.server.core.data.persistent.access.user;

import java.util.Set;

public interface UserRoleDao {

    Set<String> findUserRoles(String userId);
}
