package com.cl.mdd.server.core.manager.user;

import com.cl.mdd.server.core.data.persistent.model.specialty.CertificateType;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Set;

public class SubcategoryManagerImplTest {

    @Spy
    @InjectMocks
    private SubcategoryManagerImpl testClass = new SubcategoryManagerImpl();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMandatoryCertificatesForSubcategory() {
        SubCategory subCategory = new SubCategory();
        CertificateType da = new CertificateType();
        da.setId(CertificateType.DAC);
        da.setOptional(true);
        subCategory.getCertificateTypes().add(da);
        CertificateType rda = new CertificateType();
        rda.setId(CertificateType.RDA);
        rda.setOptional(false);
        subCategory.getCertificateTypes().add(rda);

        Set<String> result = testClass.mandatoryCertificatesForSubcategory(subCategory);

        Assert.assertEquals(result.size(), 1);
    }
}