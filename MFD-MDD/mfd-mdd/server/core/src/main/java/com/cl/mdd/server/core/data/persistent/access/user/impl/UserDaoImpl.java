package com.cl.mdd.server.core.data.persistent.access.user.impl;

import com.cl.mdd.server.core.data.persistent.access.user.UserRoleDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Repository
public class UserDaoImpl implements UserRoleDao {

    private static final Set<String> SUPER_USER_ROLES = Collections.singleton(User.ROLE_SUPER_USER);

    @Value("${system.super.user.id}")
    protected String superUserId;

    @PersistenceContext
    protected EntityManager em;

    @Override
    public Set<String> findUserRoles(String userId) {
        if (superUserId.equals(userId)) {
            return SUPER_USER_ROLES;
        }

        return queryRolesByUser(userId);
    }

    protected Set<String> queryRolesByUser(String userId) {
        /*
         * For some reasons the hsql add a space after role name, for integration tests purpose the trims are added.
         */
        Query query = em.createQuery("select case " +
                " when type(u) = SystemUser    then trim('ROLE_SYSTEM_USER')" +
                " when type(u) = PracticeOwner then trim('ROLE_PRACTICE_OWNER')" +
                " when type(u) = Professional  then trim('ROLE_PROFESSIONAL') " +
                " else 'ROLE_ANONYMOUS' end " +
                " from User u " +
                " where u.id = :userId");

        query.setParameter("userId", userId);

        return new HashSet<>(query.getResultList());
    }
}
