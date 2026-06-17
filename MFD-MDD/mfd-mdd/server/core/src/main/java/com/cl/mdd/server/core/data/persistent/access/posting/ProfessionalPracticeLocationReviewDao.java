package com.cl.mdd.server.core.data.persistent.access.posting;

import com.cl.mdd.server.core.data.model.query.model.ProfessionalToLocationReviewSummaryTuple;
import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.review.ProfessionalPracticeLocationReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessionalPracticeLocationReviewDao extends AbstractDao<ProfessionalPracticeLocationReview> {

    @Query(value = "select new com.cl.mdd.server.core.data.model.query.model.ProfessionalToLocationReviewSummaryTuple(" +
            "pplr.id," +
            "jobPosting.name," +
            "professional.id," +
            "contact.name.first," +
            "contact.name.last," +
            "location.name," +
            "jobPosting.startDate," +
            "jobPosting.endDate," +
            "pplr.rate," +
            "pplr.wouldWorkPermanently," +
            "(case when exists(select 1 from BlackListedPracticeLocation bll where bll.professional = professional and bll.location = location and bll.unblackListedDate is null) then true else false end)," +
            "pplr.comment," +
            "pplr.created" +
            ") from ProfessionalPracticeLocationReview pplr join pplr.application application " +
            "                                               join application.professional professional " +
            "                                               join professional.contact contact " +
            "                                               join application.temporaryJobPosting jobPosting " +
            "                                               join jobPosting.location location " +
            "                                               join location.practice practice " +
            "   where practice.id=:practiceId ",
            countQuery = "select count(pplr) from ProfessionalPracticeLocationReview pplr join pplr.application application " +
                    "                                               join application.temporaryJobPosting jobPosting " +
                    "                                               join jobPosting.location location " +
                    "                                               join location.practice practice " +
                    "   where practice.id=:practiceId")
    Page<ProfessionalToLocationReviewSummaryTuple> locationReviews(@Param("practiceId") String practiceId, Pageable pageable);

    List<ProfessionalPracticeLocationReview> findLast25ByApplicationJobPostingLocationIdOrderByCreated(String id);
}
