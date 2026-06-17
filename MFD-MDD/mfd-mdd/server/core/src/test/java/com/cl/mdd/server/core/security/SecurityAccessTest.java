package com.cl.mdd.server.core.security;

import com.cl.mdd.server.core.data.security.UserPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class SecurityAccessTest {

    @Spy
    @InjectMocks
    private SecurityAccess securityAccess;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Authentication sourceAuthentication;

    @Mock
    private UserDetailsService userDetailsService;

    private UserPrincipal currentUser;

    private UserPrincipal impersonatorUser;

    @Before
    public void setUp() throws Exception {
        securityAccess = new SecurityAccess();

        SecurityContextHolder.setContext(securityContext);

        currentUser = new UserPrincipal("CURRENT_USER_ID", "A", "B", true, Collections.emptySet());
        impersonatorUser = new UserPrincipal("IMPERSONATOR_ID", "B", "C", true, Collections.emptySet());
    }

    @Test
    public void currentRealUserId_whenAuthenticationIsNull_returnNull() {
        doReturn(null).when(securityContext).getAuthentication();

        assertNull(securityAccess.currentRealUserId());
    }

    @Test
    public void currentRealUserId_whenAuthenticationPrincipalIsNotUserPrincipal_returnNull() {
        doReturn(authentication).when(securityContext).getAuthentication();
        doReturn(new Object()).when(authentication).getPrincipal();

        assertNull(securityAccess.currentRealUserId());
    }

    @Test
    public void currentRealUserId_whenSwitchUserAuthorityIsNotPresent_returnCurrentUserId() {
        doReturn(authentication).when(securityContext).getAuthentication();
        doReturn(currentUser).when(authentication).getPrincipal();
        doReturn(Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_USER_2")
        )).when(authentication).getAuthorities();

        assertEquals("CURRENT_USER_ID", securityAccess.currentRealUserId());
    }

    @Test
    public void currentRealUserId_whenSwitchUserAuthorityIsPresent_returnSourceUserId() {
        doReturn(authentication).when(securityContext).getAuthentication();
        doReturn(currentUser).when(authentication).getPrincipal();
        doReturn(Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SwitchUserGrantedAuthority("ROLE_USER_2", sourceAuthentication)
        )).when(authentication).getAuthorities();
        doReturn(impersonatorUser).when(sourceAuthentication).getPrincipal();

        assertEquals("IMPERSONATOR_ID", securityAccess.currentRealUserId());
    }
}