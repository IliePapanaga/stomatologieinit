package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.data.model.PracticeModel;
import com.cl.mdd.server.core.data.model.PracticeOwnerModel;
import com.cl.mdd.server.core.data.model.RegisterPracticeOwner;
import com.cl.mdd.server.core.data.model.UserActivateDeactivateResult;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.UserStatusChangedEvent;
import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.manager.user.PracticeOwnerManager;
import com.cl.mdd.server.core.security.annotation.RequiresSystemUserRole;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.service.contact.ContactService;
import com.cl.mdd.server.core.service.practice.PracticeService;
import com.cl.mdd.server.core.service.user.PracticeOwnerService;
import com.cl.mdd.server.core.validation.group.Register;
import com.cl.mdd.server.core.validation.group.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.Objects;

@Service
@Validated
public class PracticeOwnerServiceImpl extends ServiceSupport implements PracticeOwnerService {

    @Autowired
    private PracticeOwnerManager practiceOwnerManager;

    @Autowired
    private PracticeService practiceService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SystemUserDao systemUserDao;

    @Autowired
    private EventBus<UserStatusChangedEvent> userStatusChangedEventBus;

    @Transactional
    @Override
    @Validated(value = Register.class)
    public PracticeOwnerModel register(@Valid RegisterPracticeOwner registerPracticeOwner) {
        PracticeOwner toBeRegistered = commonConverter.toPracticeOwner(registerPracticeOwner);
        toBeRegistered.setLastActivity(ZonedDateTime.now());
        PracticeOwner registered = practiceOwnerManager.register(toBeRegistered);
        practiceService.register(registerPracticeOwner.getRegisterPractice(), registered.getId());

        return commonConverter.toPracticeOwnerModel(registered);
    }

    @Override
    public PracticeOwnerModel get(String id) {
        return commonConverter.toPracticeOwnerModel(practiceOwnerManager.get(id));
    }

    public PracticeOwnerModel get(PracticeModel practiceModel) {
        return get(practiceModel.getId());
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
    @Transactional
    @PreAuthorize("@practiceOwnerAccessAuthorizer.updateAllowed(#practiceOwnerModel.id)")
    @Validated(value = Update.class)
    public PracticeOwnerModel update(@Valid PracticeOwnerModel practiceOwnerModel) {
        PracticeOwner practiceOwner = practiceOwnerManager.get(practiceOwnerModel.getId());
        if (Objects.isNull(practiceOwner)) {
            throw new MDDException("Non existent practice owner id:" + practiceOwnerModel.getId(), "NON_EXISTENT_PRACTICE_OWNER");
        }
        contactService.updateUserContact(practiceOwnerModel.getId(), practiceOwnerModel.getContact());
        if (securityAccess.isCurrentSystemUser()) {
            practiceOwner.setComments(practiceOwnerModel.getComments());
        }

        return commonConverter.toPracticeOwnerModel(practiceOwner);
    }
}
