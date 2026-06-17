package com.cl.mdd.server.core.service.practice.impl;

import com.cl.mdd.server.core.data.model.BlackListProfessional;
import com.cl.mdd.server.core.data.model.BlackListedProfessionalDetails;
import com.cl.mdd.server.core.data.model.PracticeModel;
import com.cl.mdd.server.core.data.model.RegisterPractice;
import com.cl.mdd.server.core.data.model.common.SpecialityModel;
import com.cl.mdd.server.core.data.model.query.FindAllBlackListedProfessionalDetailsQuery;
import com.cl.mdd.server.core.data.model.query.FindSystemUserPractices;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.model.query.model.SystemUserPracticeModel;
import com.cl.mdd.server.core.data.persistent.access.user.BlackListedProfessionalDao;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.professional.BlackListedProfessional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.BlackListedProfessionalId;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import com.cl.mdd.server.core.manager.user.PracticeManager;
import com.cl.mdd.server.core.manager.user.PracticeOwnerManager;
import com.cl.mdd.server.core.manager.user.ProfessionalManager;
import com.cl.mdd.server.core.security.annotation.RequiresSystemUserRole;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.service.practice.PracticeService;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
public class PracticeServiceImpl extends ServiceSupport implements PracticeService {

    @Autowired
    private PracticeManager practiceManager;

    @Autowired
    private ProfessionalManager professionalManager;

    @Autowired
    private PracticeOwnerManager practiceOwnerManager;

    @Autowired
    private QueryConverter queryConverter;

    @Autowired
    private BlackListedProfessionalDao blackListedProfessionalDao;

    @Override
    public PracticeModel register(RegisterPractice registerPractice, String ownerId) {
        Validate.notBlank(ownerId);
        PracticeOwner practiceOwner = practiceOwnerManager.get(ownerId);
        Validate.notNull(practiceOwner, "Non existent owner id: " + ownerId);
        Practice practice = commonConverter.toPractice(registerPractice);
        practice.setOwner(practiceOwner);
        //TODO REMOVE WHEN LIFECYCLE APPEARS
        practice.setStatus("INCOMPLETE");
        Practice saved = practiceManager.save(practice);

        return commonConverter.toPracticeModel(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("@practiceAccessAuthorizer.updateAllowed(#updatePractice.id)")
    public void update(PracticeModel updatePractice) {
        if (nonNull(updatePractice.getId())) {
            Practice practice = practiceManager.get(updatePractice.getId());
            Practice updated = commonConverter.toPractice(updatePractice, practice);
            practiceManager.save(updated);
        }
    }

    @Override
    public PracticeModel getById(String id) {
        Practice practice = practiceManager.get(id);
        return commonConverter.toPracticeModel(practice);
    }

    @Override
    public PracticeModel getByOwnerUsername(String username) {
        Practice db = practiceManager.findOneByOwnerUsername(username);
        return commonConverter.toPracticeModel(db);
    }

    public List<SpecialityModel> getPracticeSpecialities(PracticeModel practiceModel) {
        return practiceManager.getPracticeSpecialities(practiceModel.getId()).stream()
                .map(commonConverter::toSpecialityModel)
                .collect(Collectors.toList());
    }

    @RequiresSystemUserRole
    @Override
    public QueryResult<SystemUserPracticeModel> getSystemUserPractices(FindSystemUserPractices queryInfo) {
        FindSystemUserPractices.FindSystemUserPracticesFilter filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        Page<SystemUserPracticeModel> systemUserPractices = practiceManager.getSystemUserPractices(filters.getLastActivityFrom(), filters.getLastActivityTo(),
                filters.getNewClientsFrom(), filters.getNewClientsTo(), filters.getDistance(), filters.getLat(), filters.getLng(), filters.getSpecialties(), filters.getBlacklisted(), filters.getNameStartsWith(), filters.getTextSearch(), pageable);

        return queryConverter.toQueryResult(systemUserPractices, Function.identity());
    }

    @Override
    @Transactional
    @PreAuthorize("@practiceAccessAuthorizer.updateAllowed(#blackList.practiceId)")
    public void blackList(@Valid BlackListProfessional blackList) {
        Professional professional = professionalManager.findOne(blackList.getProfessionalId());
        Practice practice = practiceManager.get(blackList.getPracticeId());
        blackListedProfessionalDao.save(new BlackListedProfessional(practice, professional).setDate(ZonedDateTime.now()));
    }

    @Override
    @Transactional
    @PreAuthorize("@practiceAccessAuthorizer.updateAllowed(#blackList.practiceId)")
    public void unBlackList(@Valid BlackListProfessional blackList) {
        blackListedProfessionalDao.delete(new BlackListedProfessionalId(blackList.getPracticeId(), blackList.getProfessionalId()));
    }

    @Override
    @Transactional(readOnly = true)
    @RequiresSystemUserRole
    public QueryResult<BlackListedProfessionalDetails> fetch(FindAllBlackListedProfessionalDetailsQuery query) {
        FindAllBlackListedProfessionalDetailsQuery.FindAllBlackListedProfessionalDetailsFilters filters = query.getFilters();
        Pageable pageable = queryConverter.toPageable(query.getPagination());
        Page<BlackListedProfessional> blackListedProfessionals = blackListedProfessionalDao.fetchBlackListedProfessionals(filters.getProfessionalId(), pageable);
        return queryConverter.toQueryResult(blackListedProfessionals, commonConverter::toBlackListedProfessionalDetails);
    }

}
