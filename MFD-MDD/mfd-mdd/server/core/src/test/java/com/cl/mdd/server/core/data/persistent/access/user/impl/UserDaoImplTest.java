package com.cl.mdd.server.core.data.persistent.access.user.impl;

import com.cl.mdd.server.core.data.persistent.model.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class UserDaoImplTest {
    
    @InjectMocks
    private UserDaoImpl userDao = new UserDaoImpl();
    
    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @Before
    public void setUp() throws Exception {
        userDao.superUserId = "SUPER_USER_ID";

        doReturn(query).when(entityManager).createQuery(anyString());
    }

    @Test
    public void findUserRoles_whenUserIdIsSuperUserId_returnSuperAndSystemRole() {
        Set<String> result = userDao.findUserRoles("SUPER_USER_ID");

        assertTrue(result.contains(User.ROLE_SUPER_USER));

        verifyZeroInteractions(entityManager, query);
    }

    @Test
    public void findUserRoles_whenUserIdIsNotSuperUser_queryRolesFromDB() {
        doReturn(Arrays.asList(User.ROLE_PRACTICE_OWNER)).when(query).getResultList();

        Set<String> result = userDao.findUserRoles("OTHER_USER_ID");

        assertTrue(result.contains(User.ROLE_PRACTICE_OWNER));

        verify(entityManager).createQuery(anyString());
        verify(query).setParameter("userId", "OTHER_USER_ID");
        verify(query).getResultList();
    }
}