package com.cl.mdd.server.core.service.security;

import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userDao;

    private RoleHierarchy roleHierarchy;

    @Autowired
    public UserDetailsServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsernameIgnoreCase(username);
        if (isNull(user)) {
            throw new UsernameNotFoundException("Invalid username!");
        }

        return new UserPrincipal(user.getId(), username, user.getPassword(), User.ACTIVE.equals(user.getStatus()), getAllReachableUserRoles(user.getId()));
    }

    private Set<String> getAllReachableUserRoles(String userId) {
        Set<String> userRoles = userDao.findUserRoles(userId);

        if (roleHierarchy == null) {
            return userRoles;
        }

        List<GrantedAuthority> directGrantedAuthorities = emptyIfNull(userRoles).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        Collection<? extends GrantedAuthority> reachableGrantedAuthorities = roleHierarchy.getReachableGrantedAuthorities(directGrantedAuthorities);

        return reachableGrantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    @Autowired(required = false)
    public void setRoleHierarchy(RoleHierarchy roleHierarchy) {
        this.roleHierarchy = roleHierarchy;
    }
}
