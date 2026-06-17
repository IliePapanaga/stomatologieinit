package com.cl.mdd.server.core.data.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

/**
 * Security layer user details. Should be exposed only in internal services.
 */
public class UserPrincipal extends User {

    private final Set<String> roles;

    private String id;

    public UserPrincipal(String id, String username, String password, boolean enabled, Set<String> roles) {
        super(username, password, enabled, true, true, true, emptyIfNull(roles).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
        this.id = id;
        this.roles = roles;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public String getId() {
        return id;
    }

}
