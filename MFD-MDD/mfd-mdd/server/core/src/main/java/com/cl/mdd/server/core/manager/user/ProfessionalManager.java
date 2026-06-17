package com.cl.mdd.server.core.manager.user;

import com.cl.mdd.server.core.data.model.NoShowModel;
import com.cl.mdd.server.core.data.model.query.FindSystemUserProfessionals;
import com.cl.mdd.server.core.data.model.query.model.SystemUserProfessionalModel;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.manager.GenericSingleEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;

public interface ProfessionalManager extends GenericSingleEntityManager<Professional> {

    Professional findOne(String id);

    Professional getOne(String id);

    Page<SystemUserProfessionalModel> getSystemUserProfessionals(ZonedDateTime lastActivityFrom, ZonedDateTime lastActivityTo,
                                                                 ZonedDateTime newComersFrom, ZonedDateTime newComersTo, Double distance, Double lat, Double lng, List<String> specialties, String status, FindSystemUserProfessionals.ProblematicFilter problematic, String nameStartsWith, String textSearch, Pageable pageable);

    Page<NoShowModel> getProfessionalNoShow(String professionalId, Pageable pageable);

    void updateRejectionsCounter(Professional professional);

    void updateRating(Professional professional, double rating);

    boolean professionalHasCategory(String professionalId, String categoryId);
}
