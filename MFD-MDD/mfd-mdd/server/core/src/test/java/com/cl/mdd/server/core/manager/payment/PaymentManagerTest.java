package com.cl.mdd.server.core.manager.payment;

import com.cl.mdd.server.core.data.persistent.access.payment.PaymentDao;
import com.cl.mdd.server.core.data.persistent.access.posting.JobDayDao;
import com.cl.mdd.server.core.data.persistent.model.specialty.Category;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.settings.Settings;
import com.cl.mdd.server.core.settings.SystemSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentManagerTest {
    
    @InjectMocks
    private PaymentManager manager;

    @Mock
    private JobDayDao jobDayDao;

    @Mock
    private PaymentDao paymentDao;

    @Mock
    private SystemSettings systemSettings;

    private Category category = new Category();
    private SubCategory subCategory = new SubCategory();

    private BigDecimal testRate(Settings.Setting<Long> setting, long fee) {
        subCategory.setCategory(category);
        reset(systemSettings);
        when(systemSettings.get(setting)).thenReturn(fee);
        return manager.rate(subCategory);
    }

    @Test
    public void testRateDefault() {
        assertEquals(new BigDecimal(PaymentManager.DEFAULT_FEE), testRate(null, 0));
    }

    @Test
    public void testRateH() {
        category.setId("HYGIENISTS");
        assertEquals(new BigDecimal(11), testRate(Settings.FeeTemporaryJobSettings.COMPENSATION_RDH, 11));
    }

    @Test
    public void testRateA() {
        category.setId("ASSISTANTS");
        assertEquals(new BigDecimal(12), testRate(Settings.FeeTemporaryJobSettings.COMPENSATION_RDA, 12));
    }

    @Test
    public void testRateFO() {
        category.setId("FRONT_OFFICE_PERSONNEL");
        assertEquals(new BigDecimal(13), testRate(Settings.FeeTemporaryJobSettings.COMPENSATION_RDA, 13));
    }

    @Test
    public void testRateD() {
        subCategory.setId("GENERAL_DENTIST");
        assertEquals(new BigDecimal(14), testRate(Settings.FeeTemporaryJobSettings.COMPENSATION_DDS, 14));
    }

    @Test
    public void testRateS() {
        category.setId("DENTISTS");
        assertEquals(new BigDecimal(15), testRate(Settings.FeeTemporaryJobSettings.COMPENSATION_SPECIALIST, 15));
    }

}
