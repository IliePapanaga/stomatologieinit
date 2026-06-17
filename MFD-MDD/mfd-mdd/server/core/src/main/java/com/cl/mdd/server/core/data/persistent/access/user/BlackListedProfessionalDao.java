package com.cl.mdd.server.core.data.persistent.access.user;

import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.professional.BlackListedProfessional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.BlackListedProfessionalId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListedProfessionalDao extends JpaRepository<BlackListedProfessional, BlackListedProfessionalId> {

    @Query("select blp from BlackListedProfessional blp join blp.professional professional " +
            "                                           join blp.practice practice" +
            "                                           join practice.owner owner" +
            "                                           join owner.contact ownerContact" +
            "                                           join professional.contact professionalContact" +
            " where professional.id = :professionalId")
    Page<BlackListedProfessional> fetchBlackListedProfessionals(@Param("professionalId") String professionalId, Pageable pageable);

    @Query("select case when count(blp) > 0 then true else false end" +
            " from BlackListedProfessional blp join blp.professional professional " +
            "                                           join blp.practice practice" +
            " where professional.id = :professionalId and :practiceLocation member of blp.practice.locations")
    boolean isProfessionalBlacklistedInPractice(@Param("professionalId") String professionalId, @Param("practiceLocation") PracticeLocation practiceLocation);
}
