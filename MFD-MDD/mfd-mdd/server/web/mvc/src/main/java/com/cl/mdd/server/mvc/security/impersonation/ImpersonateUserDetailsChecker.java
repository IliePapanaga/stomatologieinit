package com.cl.mdd.server.mvc.security.impersonation;

import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.google.common.collect.ImmutableSet;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class ImpersonateUserDetailsChecker extends AccountStatusUserDetailsChecker {

    private static final Set<String> SWITCH_USER_ALLOWED_AUTHORITIES = ImmutableSet.of(User.ROLE_PRACTICE_OWNER, User.ROLE_PROFESSIONAL);

    @Override
    public void check(UserDetails user) {
        super.check(user);

        if (!hasAllowedAuthority(user)) {
            throw new InvalidImpersonatedUserTypeException("User does not have roles that can be impersonated");
        }
    }

    private boolean hasAllowedAuthority(UserDetails user) {
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        return emptyIfNull(authorities).stream()
                .anyMatch(authority -> SWITCH_USER_ALLOWED_AUTHORITIES.contains(authority.getAuthority()));
    }
}
