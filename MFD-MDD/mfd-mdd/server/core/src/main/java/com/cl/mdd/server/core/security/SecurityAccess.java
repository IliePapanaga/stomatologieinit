package com.cl.mdd.server.core.security;

import com.cl.mdd.server.core.data.security.UserPrincipal;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.cl.mdd.server.core.data.persistent.model.user.User.*;

/**
 * Facade for Security Context
 */
public class SecurityAccess {

    @Autowired
    private UserDetailsService userDetailsService;

    protected SecurityContext currentSecurityContext() {
        return SecurityContextHolder.getContext();
    }

    public boolean currentUserAnonymous() {
        Authentication auth = currentSecurityContext().getAuthentication();
        return auth instanceof AnonymousAuthenticationToken;
    }

    public boolean isCurrentSystemUser() {
        return currentUserRoles().contains(ROLE_SYSTEM_USER);
    }

    public Set<String> currentUserRoles() {
        UserPrincipal principal = principal();
        return principal != null ? principal.getRoles() : ImmutableSet.of();
    }

    public boolean isCurrentPracticeOwner() {
        return currentUserRoles().contains(ROLE_PRACTICE_OWNER);
    }

    public boolean isCurrentProfessional() {
        return currentUserRoles().contains(ROLE_PROFESSIONAL);
    }

    public String currentUserId() {
        UserPrincipal principal = principal();
        return getPrincipalId(principal);
    }

    private String getPrincipalId(UserPrincipal principal) {
        return principal == null ? null : principal.getId();
    }

    /**
     * @return if current user is impersonated - id of the impersonator, otherwise id of the current user
     * NOTE: use {@link SecurityAccess#currentUserId()} if it doesn't matter whether user is impersonated or not
     */
    public String currentRealUserId() {
        UserPrincipal principal = principal();

        if (principal == null) {
            return null;
        }

        Collection<? extends GrantedAuthority> authorities = currentSecurityContext().getAuthentication().getAuthorities();

        Optional<? extends GrantedAuthority> switchUserAuthority =
                authorities.stream().filter(authority -> authority instanceof SwitchUserGrantedAuthority).findAny();

        if (switchUserAuthority.isPresent()) {
            SwitchUserGrantedAuthority switchUser = (SwitchUserGrantedAuthority) switchUserAuthority.get();
            principal = getUserPrincipalFromAuthentication(switchUser.getSource());
        }

        return getPrincipalId(principal);
    }

    public boolean currentUserImpersonated(){
        return !Objects.equals(currentRealUserId(), currentUserId());
    }

    private UserPrincipal principal() {
        Authentication authentication = currentSecurityContext().getAuthentication();

        return getUserPrincipalFromAuthentication(authentication);
    }

    private UserPrincipal getUserPrincipalFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) authentication.getPrincipal();
        } else {
            return null;
        }
    }

    public void login(String username) {
        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(username);
        currentSecurityContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    public void logout(String username) {

    }

}
