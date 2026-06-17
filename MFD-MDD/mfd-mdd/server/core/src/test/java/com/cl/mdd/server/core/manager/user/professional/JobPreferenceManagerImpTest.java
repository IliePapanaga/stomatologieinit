package com.cl.mdd.server.core.manager.user.professional;

import com.cl.mdd.server.core.data.persistent.access.prodessional.JobPreferenceDao;
import com.cl.mdd.server.core.data.persistent.access.user.ProfessionalDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.ProfessionalJobPreference;
import com.cl.mdd.server.core.security.SecurityAccess;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class JobPreferenceManagerImpTest {

    @Spy
    @InjectMocks
    private JobPreferenceManagerImp manager = new JobPreferenceManagerImp();

    @Mock
    private JobPreferenceDao jobPreferenceDao;

    @Mock
    private ProfessionalDao professionalDao;

    @Mock
    private SecurityAccess securityAccess;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(securityAccess.currentUserId()).thenReturn(RandomStringUtils.randomAlphanumeric(50));
    }

    @Test
    public void testUpdatePreference() {
        ProfessionalJobPreference preference = new ProfessionalJobPreference();
        Professional professional = new Professional();

        Mockito.when(professionalDao.findByUsernameIgnoreCase(securityAccess.currentUserId())).thenReturn(professional);

        manager.updateJobPreference(professional, preference);

        Mockito.verify(manager).save(preference);
        Assert.assertSame(preference.getProfessional(), professional);
    }

    @Test
    public void testSaveePreference() {
        ProfessionalJobPreference preference = new ProfessionalJobPreference();

        Professional professional = new Professional();

        Mockito.when(professionalDao.findByUsernameIgnoreCase(securityAccess.currentUserId())).thenReturn(professional);
        Mockito.when(jobPreferenceDao.findOne(professional.getId())).thenReturn(null);

        manager.updateJobPreference(professional, preference);

        Mockito.verify(manager).save(preference);
        Assert.assertSame(preference.getProfessional(), professional);
        Assert.assertNull(preference.getId());
    }
}