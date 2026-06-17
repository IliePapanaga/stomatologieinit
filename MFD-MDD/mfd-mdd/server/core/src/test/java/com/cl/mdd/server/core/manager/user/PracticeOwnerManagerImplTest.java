package com.cl.mdd.server.core.manager.user;

import com.cl.mdd.server.core.data.persistent.access.user.PracticeOwnerDao;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static com.cl.mdd.server.core.data.persistent.model.user.User.EMAIL_CONFIRMATION_PENDING;

public class PracticeOwnerManagerImplTest {

    @Spy
    @InjectMocks
    private PracticeOwnerManagerImpl testClass = new PracticeOwnerManagerImpl();

    @Mock
    private PracticeOwnerDao practiceOwnerDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegister() {
        PracticeOwner practiceOwner = new PracticeOwner();
        Mockito.when(practiceOwnerDao.save(practiceOwner)).thenReturn(practiceOwner);

        PracticeOwner result = testClass.register(practiceOwner);

        Assert.assertSame(result, practiceOwner);
        Assert.assertSame(result.getStatus(), EMAIL_CONFIRMATION_PENDING);
    }

}