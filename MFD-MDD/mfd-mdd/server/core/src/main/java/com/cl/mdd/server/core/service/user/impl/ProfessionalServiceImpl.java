package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.query.FindAllBlackListedLocationDetailsQuery;
import com.cl.mdd.server.core.data.model.query.FindAllBlackListedLocationSummaryQuery;
import com.cl.mdd.server.core.data.model.query.FindSystemUserProfessionals;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.model.query.model.SystemUserProfessionalModel;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.data.persistent.access.practice.PracticeLocationDao;
import com.cl.mdd.server.core.data.persistent.access.prodessional.ProfessionalProfileDao;
import com.cl.mdd.server.core.data.persistent.access.user.BlackListedLocationDao;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.professional.BlackListedPracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.ProfessionalJobPreference;
import com.cl.mdd.server.core.data.persistent.model.user.professional.profile.ProfessionalProfile;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.UserStatusChangedEvent;
import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import com.cl.mdd.server.core.manager.user.ProfessionalManager;
import com.cl.mdd.server.core.manager.user.professional.JobPreferenceManager;
import com.cl.mdd.server.core.security.annotation.RequiresSystemUserRole;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.service.contact.ContactService;
import com.cl.mdd.server.core.service.user.ProfessionalService;
import com.cl.mdd.server.core.validation.group.Save;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.function.Function;

import static com.cl.mdd.server.core.data.persistent.model.user.User.EMAIL_CONFIRMATION_PENDING;
import static java.util.Objects.isNull;

// TODO ADD VALIDATION
@Service
@Validated
public class ProfessionalServiceImpl extends ServiceSupport implements ProfessionalService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProfessionalManager professionalManager;

    @Autowired
    private ProfessionalProfileDao professionalProfileDao;

    @Autowired
    private BlackListedLocationDao blackListedLocationDao;

    @Autowired
    private PracticeLocationDao practiceLocationDao;

    @Autowired
    private JobPreferenceManager jobPreferenceManager;

    @Autowired
    private QueryConverter queryConverter;

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SystemUserDao systemUserDao;

    @Autowired
    private EventBus<UserStatusChangedEvent> userStatusChangedEventBus;

    /**
     * Internal register operation.
     * Do not expose to graph QL
     *
     * @param registerUser
     * @return registered professional
     */
    @Override
    public ProfessionalModel register(RegisterProfessional registerUser) {
        Professional professional = commonConverter.toProfessional(registerUser);
        professional.setLastActivity(ZonedDateTime.now());
        professional.setStatus(EMAIL_CONFIRMATION_PENDING);
        Professional persisted = professionalManager.save(professional);
        return commonConverter.toProfessionalModel(persisted);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@professionalAccessAuthorizer.readProfileAllowed(#id)")
    public ProfessionalModel get(String id) {
        Professional db = professionalManager.findOne(id);
        return db == null ? null : commonConverter.toProfessionalModel(db);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@professionalAccessAuthorizer.readProfileAllowed(#id)")
    public ProfessionalJobPreferenceModel getProfessionalJobPreference(String id) {
        ProfessionalJobPreference db = jobPreferenceManager.findOne(id);
        return commonConverter.toProfessionalJobPreferenceModel(db);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@professionalAccessAuthorizer.readProfileAllowed(#id)")
    public ProfessionalProfileModel getProfessionalProfile(String id) {
        ProfessionalProfile db = professionalProfileDao.findOne(id);
        return commonConverter.toProfessionalProfileModel(db);
    }

    @Override
    @Transactional
    @Validated(Save.class)
    @PreAuthorize("@professionalAccessAuthorizer.updateAllowed(#professionalId)")
    public void updateJobPreferences(String professionalId, @Valid ProfessionalJobPreferenceModel request) {
        ProfessionalJobPreference preference = commonConverter.toProfessionalJobPreference(request);
        jobPreferenceManager.updateJobPreference(professionalManager.getOne(professionalId), preference);
    }

    @Override
    @Transactional
    @Validated(value = Save.class)
    @PreAuthorize("@professionalAccessAuthorizer.updateAllowed(#professionalId)")
    public void updateProfile(String professionalId, ProfessionalProfileModel professionalProfileModel) {
        ProfessionalProfile dbProfile = professionalProfileDao.findOne(professionalId);
        if (isNull(dbProfile)) {
            dbProfile = new ProfessionalProfile();
            dbProfile.setProfessional(professionalManager.getOne(professionalId));
        }
        commonConverter.toProfessionalProfile(dbProfile, professionalProfileModel);
        professionalProfileDao.saveAndFlush(dbProfile);
    }

    @Override
    @Transactional
    @PreAuthorize("@professionalAccessAuthorizer.updateAllowed(#professionalModel.id)")
    public ProfessionalModel update(@Valid ProfessionalModel professionalModel) {
        String id = professionalModel.getId();
        Professional professional = professionalManager.findOne(id);
        if (isNull(professional)) {
            throw new MDDException("Non existent professional id:" + id, "NON_EXISTENT_PROFESSIONAL");
        }

        professional.setNotificationsEnabled(professionalModel.isNotificationsEnabled());
        if (securityAccess.isCurrentSystemUser()) {
            professional.setComments(professionalModel.getComments());
        }
        professionalManager.save(professional);

        contactService.updateUserContact(id, professionalModel.getContact());

        return commonConverter.toProfessionalModel(professional);
    }

    @Override
    @Transactional
    @PreAuthorize("@professionalAccessAuthorizer.updateAllowed(#blackList.professionalId)")
    public void blackList(@Valid BlackListPracticeLocation blackList) {
        if (blackListedLocationDao.isLocationBlacklistedByProfessional(blackList.getProfessionalId(), blackList.getPracticeLocationId())) {
            return;
        }
        Professional professional = professionalManager.findOne(blackList.getProfessionalId());
        PracticeLocation location = practiceLocationDao.findOne(blackList.getPracticeLocationId());
        blackListedLocationDao.save(new BlackListedPracticeLocation(professional, location).setBlackListedDate(ZonedDateTime.now()));
    }

    @Override
    @Transactional
    @PreAuthorize("@professionalAccessAuthorizer.updateAllowed(#blackList.professionalId)")
    public void unBlackList(@Valid BlackListPracticeLocation blackList) {
        blackListedLocationDao.unblackList(blackList.getProfessionalId(), blackList.getPracticeLocationId());
    }

    @RequiresSystemUserRole
    @Transactional(readOnly = true)
    @Override
    public QueryResult<SystemUserProfessionalModel> getSystemUserProfessionals(FindSystemUserProfessionals queryInfo) {
        FindSystemUserProfessionals.FindSystemUserProfessionalsFilter filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        Page<SystemUserProfessionalModel> systemUserProfessionals = professionalManager.getSystemUserProfessionals(filters.getLastActivityFrom(),
                filters.getLastActivityTo(),
                filters.getNewComersFrom(),
                filters.getNewComersTo(),
                filters.getDistance(),
                filters.getLat(),
                filters.getLng(),
                filters.getSpecialties(),
                filters.getStatus(),
                filters.getProblematic(),
                filters.getNameStartsWith(),
                filters.getTextSearch(),
                pageable);

        return queryConverter.toQueryResult(systemUserProfessionals, Function.identity());
    }

    @Override
    @RequiresSystemUserRole
    public UserActivateDeactivateResult activateDeactivateAccount(String id, boolean enabled) {
        Boolean updated = executeInTransaction(() -> (enabled ? userDao.activate(id, systemUserDao.findOne(securityAccess.currentUserId())) : userDao.deactivate(id)) != 0);

        if (updated && !enabled) {
            securityAccess.logout(userDao.findUsernameById(id));
        }

        String newStatus = userDao.findUserStatusById(id);

        userStatusChangedEventBus.publishEvent(event -> {
            event.setUserId(id);
            event.setStatus(newStatus);
        });

        return new UserActivateDeactivateResult(id, newStatus);
    }

    @Override
    @Transactional(readOnly = true)
    @RequiresSystemUserRole
    public QueryResult<BlackListedLocationDetails> fetch(FindAllBlackListedLocationDetailsQuery queryInfo) {
        FindAllBlackListedLocationDetailsQuery.FindAllBlackListedLocationDetailsFilters filter = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        Page<BlackListedPracticeLocation> blackListedProfessionals = blackListedLocationDao.fetchBlackListedLocations(filter.getProfessionalId(), pageable);
        return queryConverter.toQueryResult(blackListedProfessionals, commonConverter::toBlackListedLocationDetails);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@professionalAccessAuthorizer.readAllowed(#queryInfo.filters.professionalId)")
    public QueryResult<BlackListedLocationSummary> fetch(FindAllBlackListedLocationSummaryQuery queryInfo) {
        FindAllBlackListedLocationSummaryQuery.FindAllBlackListedLocationSummaryFilters filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        Page<BlackListedPracticeLocation> blackListedProfessionals = blackListedLocationDao.fetchBlackListedLocations(filters.getProfessionalId(), pageable);
        return queryConverter.toQueryResult(blackListedProfessionals, commonConverter::toBlackListedLocationSummary);
    }
}
