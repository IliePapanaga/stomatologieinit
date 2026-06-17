package com.cl.mdd.server.core.data.persistent.listeners;

import com.cl.mdd.server.core.security.SecurityAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import java.util.Objects;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Autowired
    private SecurityAccess securityAccess;

    public String getCurrentAuditor() {
        String userId = securityAccess.currentRealUserId();
        return Objects.isNull(userId) ? "ANONYMOUS" : userId;
    }
}