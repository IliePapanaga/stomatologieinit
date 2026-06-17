package com.cl.mdd.server.core.manager.user;

import com.cl.mdd.server.core.data.model.geocoding.GeocodingArea;
import com.cl.mdd.server.core.data.model.query.model.SystemUserPracticeModel;
import com.cl.mdd.server.core.data.persistent.access.practice.PracticeDao;
import com.cl.mdd.server.core.data.persistent.model.common.Speciality;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import com.cl.mdd.server.core.manager.annotation.Manager;
import com.cl.mdd.server.core.service.geocoding.GeocodingUtils;
import com.cl.mdd.server.core.validation.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;

@Manager
public class PracticeManagerImpl implements PracticeManager {

    @Autowired
    private PracticeDao practiceDao;

    @Autowired
    private GeocodingUtils geocodingUtils;
    @Autowired
    private ValidationUtils validationUtils;

    @Override
    public Practice get(String id) {
        return practiceDao.findOne(id);
    }

    @Override
    public Practice save(Practice practice) {
        return practiceDao.save(practice);
    }

    @Override
    public List<Speciality> getPracticeSpecialities(String id) {
        return practiceDao.getPracticeSpecialities(id);
    }

    @Override
    public Practice findOneByOwnerUsername(String username) {
        return practiceDao.findOneByOwnerUsername(username);
    }

    @Override
    public Page<SystemUserPracticeModel> getSystemUserPractices(ZonedDateTime lastActivityFrom, ZonedDateTime lastActivityTo,
                                                                ZonedDateTime newClientsFrom, ZonedDateTime newClientsTo,
                                                                Double distance, Double lat, Double lng, List<String> specialties, Boolean blackListed, String nameStartsWith, String textSearch, Pageable pageable) {
        validationUtils.validateFilterParam("Invalid 'lastActivity From To' filter", "INVALID_ACTIVITY_FROM_TO_FILTER", lastActivityFrom, lastActivityTo);
        validationUtils.validateFilterParam("Invalid 'location' filter", "INVALID_LOCATION_FILTER", distance, lat, lng);
        validationUtils.validateFilterParam("Invalid 'newClients From To' filter", "INVALID_NEW_CLIENTS_FROM_TO_FILTER", newClientsFrom, newClientsTo);

        GeocodingArea area = geocodingUtils.toSquaredArea(distance, lat, lng);

        return practiceDao.getSystemUserPractices(lastActivityFrom, lastActivityTo, newClientsFrom, newClientsTo, area.getDistance(), area.getLat(), area.getLng(),
                area.getLat1(), area.getLat2(), area.getLng1(), area.getLng2(), specialties, blackListed, nameStartsWith, textSearch, pageable);
    }

}
