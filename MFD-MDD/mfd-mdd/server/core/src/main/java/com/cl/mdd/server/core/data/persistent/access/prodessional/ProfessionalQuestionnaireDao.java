package com.cl.mdd.server.core.data.persistent.access.prodessional;

import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessionalQuestionnaireDao extends AbstractDao<ProfessionalQuestionnaire> {

    @Query("select p from ProfessionalQuestionnaire q join q.professional p where q.id = :id")
    Professional findProfessionalByQuestionnaireId(@Param("id") String id);

    ProfessionalQuestionnaire findByProfessional_IdAndCategory_Id(@Param("professionalId") String professionalId,
                                                                  @Param("categoryId") String categoryId);

    @Query("select pq from ProfessionalQuestionnaire pq " +
            "where pq.professional = :professional " +
            "and pq.category not in (" +
            "   select distinct psc.category from Professional p " +
            "       join p.subCategories psc " +
            "   where p = :professional" +
            ")")
    List<ProfessionalQuestionnaire> findAllProfessionalQuestionnaireAfterSubcategoryDeleted(@Param("professional") Professional professional);
}
