package com.cl.mdd.server.mvc.config;

import com.cl.mdd.server.core.data.persistent.model.user.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.RoleHierarchyVoter;

@Configuration
public class RoleConfig {

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(User.ROLE_SUPER_USER + " > " + User.ROLE_SYSTEM_USER);
        return roleHierarchy;
    }

    @Bean
    public RoleHierarchyVoter roleHierarchyVoter() {
        return new RoleHierarchyVoter(roleHierarchy());
    }
}