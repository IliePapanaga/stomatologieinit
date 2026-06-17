package com.cl.mdd.server.core.manager.user;

import com.cl.mdd.server.core.data.model.query.Pagination;
import com.cl.mdd.server.core.data.model.query.QueryInfo;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.model.user.SystemUser;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SystemUserManagerImplTest {

    @Spy
    @InjectMocks
    private SystemUserManagerImpl testClass = new SystemUserManagerImpl();

    @Mock
    private SystemUserDao userDao;

    @Mock
    private QueryConverter queryConverter;

    @Mock
    private SystemUser systemUser;

    @Mock
    private CommonConverter commonConverter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void save() {
        SystemUser user = new SystemUser();
        SystemUser persisted = new SystemUser();
        Mockito.when(userDao.save(user)).thenReturn(persisted);

        SystemUser result = testClass.save(user);

        assertSame(result, persisted);
    }

    @Test
    public void get() {
        doReturn(systemUser).when(userDao).findOne(anyString());

        SystemUser result = testClass.get("ID");

        assertSame(systemUser, result);

        verify(userDao).findOne("ID");
    }

    @Test
    public void findAll() {
        QueryInfo queryInfo = new QueryInfo();
        Pageable pageable = new PageRequest(2, 5);
        List<SystemUser> dbData = Collections.singletonList(new SystemUser());
        Page<SystemUser> pageFromDb = new PageImpl<>(dbData);
        QueryResult queryResult = new QueryResult();

        doReturn(pageable).when(queryConverter).toPageable(any(Pagination.class), anyMapOf(String.class, String.class));
        doReturn(pageFromDb).when(userDao).findAll(pageable);
        doReturn(queryResult).when(queryConverter).toQueryResult(eq(pageFromDb), any(Function.class));

        assertSame(queryResult, testClass.findAll(queryInfo));
    }
}