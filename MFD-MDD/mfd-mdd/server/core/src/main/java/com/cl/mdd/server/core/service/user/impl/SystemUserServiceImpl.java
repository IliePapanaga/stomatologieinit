package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.data.model.RegisterSystemUser;
import com.cl.mdd.server.core.data.model.SystemUserModel;
import com.cl.mdd.server.core.data.model.UserActivateDeactivateResult;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.query.FindSystemUsersQuery;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.contact.Contact;
import com.cl.mdd.server.core.data.persistent.model.user.SystemUser;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.manager.user.SystemUserManager;
import com.cl.mdd.server.core.security.annotation.RequiresSuperUserRole;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.service.contact.ContactService;
import com.cl.mdd.server.core.service.user.SystemUserService;
import com.cl.mdd.server.core.validation.group.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiresSuperUserRole
public class SystemUserServiceImpl extends ServiceSupport implements SystemUserService {

    @Autowired
    private SystemUserManager systemUserManager;

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SystemUserDao systemUserDao;

    @Override
    @Transactional
    @Validated(Register.class)
    public SystemUserModel register(RegisterSystemUser registerSystemUser) {
        SystemUser systemUser = commonConverter.toSystemUser(registerSystemUser);
        systemUser.setStatus(User.EMAIL_CONFIRMATION_PENDING);

        SystemUser persisted = systemUserManager.save(systemUser);

        return commonConverter.toSystemUserModel(persisted);
    }

    @Override
    @Transactional(readOnly = true)
    public SystemUserModel get(String id) {
        return commonConverter.toSystemUserModel(systemUserManager.get(id));
    }

    @Override
    @Transactional
    public void update(String id, ContactModel contact) {
        SystemUser systemUser = getSystemUserById(id);

        Contact persistedContact = systemUser.getContact();
        mergeContactModel(contact, persistedContact);

        ContactModel mergedContactModel = commonConverter.toContactModel(persistedContact);

        contactService.updateUserContact(id, mergedContactModel);
    }

    private void mergeContactModel(ContactModel contact, Contact persistedContact) {
        persistedContact.getName().setFirst(contact.getName().getFirst());
        persistedContact.getName().setLast(contact.getName().getLast());
        persistedContact.setPhone(contact.getPhone());
    }

    @Override
    @Transactional(readOnly = true)
    public QueryResult<SystemUserModel> findAll(FindSystemUsersQuery queryInfo) {
        return systemUserManager.findAll(queryInfo);
    }

    @Override
    public UserActivateDeactivateResult activateDeactivateAccount(String id, boolean enabled) {
        Boolean updated = executeInTransaction(() -> (enabled ? userDao.activate(id, systemUserDao.findOne(securityAccess.currentUserId())) : userDao.deactivate(id)) != 0);

        if (updated && !enabled) {
            securityAccess.logout(userDao.findUsernameById(id));
        }
        return new UserActivateDeactivateResult(id, userDao.findUserStatusById(id));
    }

    private SystemUser getSystemUserById(String id) {
        SystemUser systemUser = systemUserManager.get(id);

        if (systemUser == null) {
            throw new MDDException("Non existent system user id:" + id, "ACCOUNT_ACTIVATE_DEACTIVATE_NON_EXISTENT_SYSTEM_USER");
        }
        return systemUser;
    }
}
