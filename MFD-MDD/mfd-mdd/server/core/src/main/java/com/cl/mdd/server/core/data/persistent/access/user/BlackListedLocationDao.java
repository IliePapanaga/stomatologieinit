package com.cl.mdd.server.core.data.persistent.access.user;

import com.cl.mdd.server.core.data.persistent.model.user.professional.BlackListedPracticeLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListedLocationDao extends JpaRepository<BlackListedPracticeLocation, String> {

    @Query("select bll from BlackListedPracticeLocation bll join bll.professional professional " +
            "                                           join bll.location location" +
            "                                           join location.practice practice" +
            "                                           join practice.owner owner" +
            "                                           join owner.contact ownerContact" +
            "                                           join professional.contact professionalContact" +
            " where professional.id = :professionalId")
    Page<BlackListedPracticeLocation> fetchBlackListedLocations(@Param("professionalId") String professionalId, Pageable pageable);

    @Query("select case when count(bll) > 0 then true else false end" +
            " from BlackListedPracticeLocation bll join bll.professional professional " +
            "                                           join bll.location location" +
            " where professional.id = :professionalId" +
            " and location.id = :practiceLocationId" +
            " and bll.unblackListedDate is null ")
    boolean isLocationBlacklistedByProfessional(@Param("professionalId") String professionalId, @Param("practiceLocationId") String practiceLocationId);

    @Query("update BlackListedPracticeLocation bll " +
            " set bll.unblackListedDate = ?#{T(java.time.ZonedDateTime).now()}" +
            " where bll.unblackListedDate is null and" +
            "       bll.professional.id = :professionalId and " +
            "       bll.location.id = :practiceLocationId")
    @Modifying(clearAutomatically = true)
    int unblackList(@Param("professionalId") String professionalId, @Param("practiceLocationId") String practiceLocationId);
}
