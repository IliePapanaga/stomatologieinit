package com.cl.mdd.server.core.manager.user;

import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;

public interface PracticeOwnerManager {
    PracticeOwner register(PracticeOwner registerUser);
    PracticeOwner get(String id);
}
