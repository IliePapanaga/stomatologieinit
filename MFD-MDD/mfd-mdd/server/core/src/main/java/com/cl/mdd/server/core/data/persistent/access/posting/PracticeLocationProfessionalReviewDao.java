package com.cl.mdd.server.core.data.persistent.access.posting;

import com.cl.mdd.server.core.data.model.query.model.LocationToProfessionalReviewSummaryTuple;
import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.review.PracticeLocationProfessionalReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeLocationProfessionalReviewDao extends AbstractDao<PracticeLocationProfessionalReview> {

    @Query(value = "select new com.cl.mdd.server.core.data.model.query.model.LocationToProfessionalReviewSummaryTuple(" +
            "plpr.id," +
            "jobPosting.name," +
            "contact.name.first," +
            "contact.name.last," +
            "location.name," +
            "jobPosting.startDate," +
            "jobPosting.endDate," +
            "plpr.professionalismRate," +
            "plpr.communicationRate," +
            "plpr.workQualityRate," +
            "plpr.punctualityRate," +
            "plpr.appearanceRate," +
            "(plpr.professionalismRate + plpr.communicationRate + plpr.workQualityRate + plpr.punctualityRate + plpr.appearanceRate)/5.0," +
            "plpr.wouldHire," +
            "case when blackListed is not null then true else false end," +
            "plpr.comment," +
            "plpr.created" +
            ") from PracticeLocationProfessionalReview plpr join plpr.application application " +
            "                                               join application.professional professional " +
            "                                               join application.temporaryJobPosting jobPosting " +
            "                                               join jobPosting.location location " +
            "                                               join location.practice practice " +
            "                                               join practice.owner owner " +
            "                                               join owner.contact contact " +
            "                                               left join practice.blackListedProfessionals blackListed on (blackListed.professional.id =:professionalId) " +
            "   where professional.id=:professionalId ",
            countQuery = "select count (plpr) from PracticeLocationProfessionalReview plpr join plpr.application application " +
                    "                                                                       join application.professional professional " +
                    "   where professional.id=:professionalId ")
    Page<LocationToProfessionalReviewSummaryTuple> professionalReviews(@Param("professionalId") String professionalId, Pageable pageable);


    List<PracticeLocationProfessionalReview> findLast25ByApplicationProfessionalIdOrderByCreated(String professionalId);
}
