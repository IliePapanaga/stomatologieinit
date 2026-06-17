package com.cl.mdd.server.core.data.persistent.access.security;

import com.cl.mdd.server.core.data.persistent.model.security.PersistentLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
public interface PersistentLoginDao extends JpaRepository<PersistentLogin, String> {

    @Query("delete from PersistentLogin pl where pl.lastUsed <:validityDate")
    @Modifying(clearAutomatically = true)
    int deleteAllOlderThan(@Param("validityDate") ZonedDateTime zonedDateTime);

}
