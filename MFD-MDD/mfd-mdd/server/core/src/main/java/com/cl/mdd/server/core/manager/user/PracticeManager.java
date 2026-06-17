package com.cl.mdd.server.core.manager.user;

import com.cl.mdd.server.core.data.model.query.model.SystemUserPracticeModel;
import com.cl.mdd.server.core.data.persistent.model.common.Speciality;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;

public interface PracticeManager {
    Practice save(Practice practice);

    Practice get(String id);

    List<Speciality> getPracticeSpecialities(String id);

    Practice findOneByOwnerUsername(String username);

    Page<SystemUserPracticeModel> getSystemUserPractices(ZonedDateTime lastActivityFrom, ZonedDateTime lastActivityTo,
                                                         ZonedDateTime newClientsFrom, ZonedDateTime newClientsTo,
                                                         Double distance, Double lat, Double lng, List<String> specialties, Boolean blackListed, String nameStartsWith, String textSearch, Pageable pageRequest);
}
