package com.cl.mdd.server.core.service.security;

import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.SystemUser;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.security.UserPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class UserDetailsServiceImplTest {

    @Spy
    @InjectMocks
    private UserDetailsServiceImpl service;

    @Mock
    private UserDao userDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void loadUserByUsernameEnabled() throws Exception {
        UserPrincipal userPrincipal = loadByUsername(User.ACTIVE);
        assertTrue(userPrincipal.isEnabled());
    }

    @Test
    public void loadUserByUsernameDisabled() throws Exception {
        UserPrincipal userPrincipal = loadByUsername(User.EMAIL_CONFIRMATION_PENDING);
        assertFalse(userPrincipal.isEnabled());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsernameWithInvalidUsername() throws Exception {
        String invalidUsername = "invalidUsername";
        doReturn(null).when(userDao).findByUsernameIgnoreCase(invalidUsername);
        service.loadUserByUsername(invalidUsername);
    }



    private UserPrincipal loadByUsername(String status) {
        Set<String> roles = new HashSet<>();
        String username = "Username";
        Professional user = new Professional();
        String id = "id";
        String pwd = "pwd";
        user.setId(id);
        user.setPassword(pwd);
        user.setStatus(status);
        doReturn(user).when(userDao).findByUsernameIgnoreCase(username);
        doReturn(roles).when(userDao).findUserRoles(id);

        UserPrincipal result = service.loadUserByUsername(username);

        verify(userDao).findByUsernameIgnoreCase(username);
        verify(userDao).findUserRoles(id);
        assertNotNull(result);
        assertSame(roles, result.getRoles());
        assertEquals(id, result.getId());
        assertEquals(username, result.getUsername());
        assertEquals(pwd, result.getPassword());

        return result;
    }

    @Test
    public void loadUserByUsername_whenRoleHierarchyIsConfigured_loadUserWithAllAccessibleAuthorities() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(User.ROLE_SUPER_USER + " > " + User.ROLE_SYSTEM_USER);

        service.setRoleHierarchy(hierarchy);

        final String username = "Username";
        final String userId = "ID";

        Set<String> userRoles = Collections.singleton(User.ROLE_SUPER_USER);

        User user = new SystemUser();
        user.setId(userId);
        user.setUsername(username);
        user.setPassword("password");

        doReturn(user).when(userDao).findByUsernameIgnoreCase(username);
        doReturn(userRoles).when(userDao).findUserRoles(userId);


        UserPrincipal result = service.loadUserByUsername(username);

        assertNotNull(result);
        assertTrue(result.getRoles().contains(User.ROLE_SUPER_USER));
        assertTrue(result.getRoles().contains(User.ROLE_SYSTEM_USER));
    }
}