package com.cl.mdd.server.core.manager.user;

import com.cl.mdd.server.core.data.persistent.access.user.PracticeOwnerDao;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.manager.annotation.Manager;
import org.springframework.beans.factory.annotation.Autowired;

import static com.cl.mdd.server.core.data.persistent.model.user.User.EMAIL_CONFIRMATION_PENDING;

@Manager
public class PracticeOwnerManagerImpl implements PracticeOwnerManager {

    @Autowired
    private PracticeOwnerDao practiceOwnerDao;

    @Override
    public PracticeOwner register(PracticeOwner practiceOwner) {
        practiceOwner.setStatus(EMAIL_CONFIRMATION_PENDING);
        return practiceOwnerDao.save(practiceOwner);
    }

    @Override
    public PracticeOwner get(String id) {
        return practiceOwnerDao.findOne(id);
    }

}
