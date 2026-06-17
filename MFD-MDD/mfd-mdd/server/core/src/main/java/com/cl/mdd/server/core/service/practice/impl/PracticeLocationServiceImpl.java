package com.cl.mdd.server.core.service.practice.impl;

import com.cl.mdd.server.core.data.model.AddPracticeLocation;
import com.cl.mdd.server.core.data.model.PracticeLocationModel;
import com.cl.mdd.server.core.data.model.PracticeModel;
import com.cl.mdd.server.core.data.model.UpdatePracticeLocation;
import com.cl.mdd.server.core.data.model.query.FindAllPracticeLocationsQuery;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.persistent.access.practice.PracticeLocationDao;
import com.cl.mdd.server.core.data.persistent.model.contact.Address;
import com.cl.mdd.server.core.data.persistent.model.contact.Contact;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import com.cl.mdd.server.core.manager.user.PracticeManager;
import com.cl.mdd.server.core.security.annotation.RequiresPracticeOwnerRole;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.service.practice.PracticeLocationService;
import com.cl.mdd.server.core.validation.group.RequireCoordinates;
import com.cl.mdd.server.core.validation.group.Save;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class PracticeLocationServiceImpl extends ServiceSupport implements PracticeLocationService {

    @Autowired
    private PracticeLocationDao practiceLocationDao;

    @Autowired
    private PracticeManager practiceManager;

    @Autowired
    private QueryConverter queryConverter;

    @Override
    @RequiresPracticeOwnerRole
    @Transactional
    @Validated({Save.class, RequireCoordinates.class})
    public PracticeLocationModel execute(@Valid AddPracticeLocation addPracticeLocation) {
        PracticeLocation practiceLocation = commonConverter.toPracticeLocation(addPracticeLocation);
        Practice currentPractice = practiceManager.get(securityAccess.currentUserId());
        practiceLocation.setPractice(currentPractice);
        PracticeLocation saved = practiceLocationDao.save(practiceLocation);
        currentPractice.getLocations().add(saved);
        userDao.updateLastActivityForCurrentUser();
        return commonConverter.toPracticeLocationModel(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("@practiceLocationAccessAuthorizer.updateAllowed(#updatePracticeLocation.id)")
    @Validated({Save.class, RequireCoordinates.class})
    public PracticeLocationModel execute(@Valid UpdatePracticeLocation updatePracticeLocation) {
        PracticeLocation update = commonConverter.toPracticeLocation(updatePracticeLocation);
        PracticeLocation db = practiceLocationDao.findOne(update.getId());
        updateLocation(update, db);
        userDao.updateLastActivityForCurrentUser();
        return commonConverter.toPracticeLocationModel(update);
    }

    protected void updateLocation(PracticeLocation from, PracticeLocation to) {
        to.setName(from.getName());
        //TODO !!! THINK HOW TO HANDLE UpdatePracticeLocation#timeZone.
        // Currently if something changes in job posting (number of job days, or start time/end time is changed, all applications are cancelled), in fact, timezone update is like a cascade start/end time update for every job posting from such a location
        // So the amount of cancelled application may be huge and it's not clear how to proceed in such case, temporarily the update of time zone is restricted
//        to.setTimeZone(from.getTimeZone());
        Contact toContact = to.getContact();
        Contact fromContact = from.getContact();

        Address toAddress = toContact.getAddress();
        Address fromContactAddress = fromContact.getAddress();

        toContact.setName(fromContact.getName());
        toContact.setFax(fromContact.getFax());
        toContact.setEmail(fromContact.getEmail());
        toContact.setPhone(fromContact.getPhone());
        toAddress.setCountry(fromContactAddress.getCountry());
        toAddress.setState(fromContactAddress.getState());
        toAddress.setCity(fromContactAddress.getCity());
        toAddress.setStreet(fromContactAddress.getStreet());
        toAddress.setZipCode(fromContactAddress.getZipCode());
    }

    @Override
    @RequiresPracticeOwnerRole
    @PreAuthorize("@practiceLocationAccessAuthorizer.deleteAllowed(#id)")
    @Transactional
    public void delete(String id) {
        practiceLocationDao.deleteById(id);
        userDao.updateLastActivityForCurrentUser();
    }

    @Override
    public List<PracticeLocationModel> getPracticeLocations(PracticeModel practiceModel) {
        return practiceLocationDao.findByPracticeId(practiceModel.getId()).stream()
                .map(commonConverter::toPracticeLocationModel)
                .collect(Collectors.toList());
    }


    @RequiresPracticeOwnerRole
    public QueryResult<PracticeLocationModel> findAllPracticeLocations(FindAllPracticeLocationsQuery queryInfo) {
        FindAllPracticeLocationsQuery.FindAllPracticeLocationsFilter filter = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        Page<PracticeLocation> practiceLocations = practiceLocationDao.findAllPracticeLocations(filter.getContactEmailLike(),
                filter.getNameLike(),
                filter.getContactPhoneLike(),
                filter.getContactFirstNameLike(),
                filter.getContactLastNameLike(),
                pageable);
        return queryConverter.toQueryResult(practiceLocations, commonConverter::toPracticeLocationModel);
    }

    @RequiresPracticeOwnerRole
    @PreAuthorize("@practiceLocationAccessAuthorizer.readAllowed(#id)")
    @Override
    public PracticeLocationModel get(String id) {
        return commonConverter.toPracticeLocationModel(practiceLocationDao.findOne(id));
    }
}
