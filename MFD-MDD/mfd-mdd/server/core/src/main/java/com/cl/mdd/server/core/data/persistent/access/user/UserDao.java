package com.cl.mdd.server.core.data.persistent.access.user;

import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.user.SystemUser;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface UserDao extends AbstractDao<User>, UserRoleDao {

    User findByUsernameIgnoreCase(String username);

    Long countByUsernameIgnoreCase(String email);

    @Query("select u from User u where u.created <:validityDate and u.status = '" + User.EMAIL_CONFIRMATION_PENDING + "'")
    List<User> findAllIncompleteUserOlderThan(@Param("validityDate") ZonedDateTime validityDate);

    @Query("update User u " +
            "set u.status = '" + User.ACTIVE + "'," +
            "    u.approvedBy = :activatedBy" +
            " where u.id =:id")
    @Modifying(clearAutomatically = true)
    int activate(@Param("id") String id, @Param("activatedBy") SystemUser systemUser);

    @Query("update User u " +
            "set u.lastActivity =  ?#{T(java.time.ZonedDateTime).now()}" +
            " where u.id = ?#{@webSecurityAccess.currentUserId()} and (?#{@webSecurityAccess.currentUserImpersonated()} = false or ?#{@environment.getProperty('user.last.activity.track.impersonation', T(java.lang.Boolean), true)} = true)")
    @Modifying
    int updateLastActivityForCurrentUser();

    @Query("update User u " +
            "set u.lastActivity =  ?#{T(java.time.ZonedDateTime).now()}" +
            " where u.id = :id and (?#{@webSecurityAccess.currentUserImpersonated()} = false or ?#{@environment.getProperty('user.last.activity.track.impersonation', T(java.lang.Boolean), true)} = true)")
    @Modifying
    int updateLastActivity(@Param("id") String id);

    @Query("update User u " +
            "set u.status = '" + User.INACTIVE + "'" +
            " where u.id =:id")
    @Modifying(clearAutomatically = true)
    int deactivate(@Param("id") String id);

    @Query("select u.status from User u where u.id =:id")
    String findUserStatusById(@Param("id") String id);

    @Query("select u.username from User u where u.id =:id")
    String findUsernameById(@Param("id") String id);

}
