package com.cl.mdd.server.core.manager.user;

import com.cl.mdd.server.core.data.model.NoShowModel;
import com.cl.mdd.server.core.data.model.geocoding.GeocodingArea;
import com.cl.mdd.server.core.data.model.query.FindSystemUserProfessionals;
import com.cl.mdd.server.core.data.model.query.model.SystemUserProfessionalModel;
import com.cl.mdd.server.core.data.persistent.access.user.ProfessionalDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.manager.annotation.Manager;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import com.cl.mdd.server.core.service.geocoding.GeocodingUtils;
import com.cl.mdd.server.core.validation.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Manager
public class ProfessionalManagerImpl implements ProfessionalManager {

    private final static Map<String, String> DICTIONARY = new HashMap<>();

    static {
        DICTIONARY.put("contactx.email", "contact.email");
    }

    @Autowired
    private CommonConverter commonConverter;

    @Autowired
    private ProfessionalDao professionalDao;

    @Autowired
    private QueryConverter queryConverter;

    @Autowired
    private ValidationUtils validationUtils;

    @Autowired
    private GeocodingUtils geocodingUtils;

    @Override
    public Professional findOne(String id) {
        return isNull(id) ? null : professionalDao.findOne(id);
    }

    @Override
    public Professional getOne(String id) {
        return professionalDao.getOne(id);
    }

    @Override
    public Professional save(Professional professional) {
        return professionalDao.save(professional);
    }

    @Override
    public Page<SystemUserProfessionalModel> getSystemUserProfessionals(ZonedDateTime lastActivityFrom,
                                                                        ZonedDateTime lastActivityTo,
                                                                        ZonedDateTime newComersFrom,
                                                                        ZonedDateTime newComersTo,
                                                                        Double distance,
                                                                        Double lat,
                                                                        Double lng,
                                                                        List<String> specialties,
                                                                        String status,
                                                                        FindSystemUserProfessionals.ProblematicFilter problematic,
                                                                        String nameStartsWith,
                                                                        String textSearch,
                                                                        Pageable pageable) {
        validationUtils.validateFilterParam("Invalid lastActivity From To filter", "INVALID_ACTIVITY_FROM_TO_FILTER", lastActivityFrom, lastActivityTo);
        validationUtils.validateFilterParam("Invalid 'location' filter", "INVALID_LOCATION_FILTER", distance, lat, lng);
        validationUtils.validateFilterParam("Invalid 'new comers From To' filter", "INVALID_NEW_COMERS_FROM_TO_FILTER", newComersFrom, newComersTo);

        GeocodingArea area = geocodingUtils.toSquaredArea(distance, lat, lng);

        String problematicFilter = problematic != null ? problematic.toString() : null;
        return professionalDao.getSystemUserProfessionals(lastActivityFrom, lastActivityTo,
                newComersFrom, newComersTo,
                area.getDistance(), area.getLat(), area.getLng(),
                area.getLat1(), area.getLat2(), area.getLng1(), area.getLng2(),
                specialties,
                status,
                problematicFilter,
                nameStartsWith,
                textSearch,
                pageable);
    }

    @Override
    public Page<NoShowModel> getProfessionalNoShow(String professionalId, Pageable pageable) {
        return professionalDao.findNoWorks(professionalId, pageable);
    }

    @Override
    public void updateRejectionsCounter(Professional professional) {
        professionalDao.updateRejectionsCounter(professional);
    }

    @Override
    public void updateRating(Professional professional, double rating) {
        professionalDao.updateRating(professional.getId(), rating);
    }

    @Override
    public boolean professionalHasCategory(String professionalId, String categoryId) {
        return professionalDao.hasCategory(professionalId, categoryId);
    }
}
