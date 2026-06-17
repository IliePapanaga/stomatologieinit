package com.cl.mdd.server.mvc.rest.professional;

import com.cl.mdd.server.core.data.model.RegisterProfessional;
import com.cl.mdd.server.core.data.model.common.ProfessionalSubcategoryModel;
import com.cl.mdd.server.core.data.model.query.RequiredCertificate;
import com.cl.mdd.server.core.data.persistent.model.specialty.CertificateType;
import com.cl.mdd.server.mvc.config.SecurityConfig;
import com.cl.mdd.server.mvc.config.WebConfig;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.system.SystemUserWorker;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@ActiveProfiles("hsqldb-local")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = {WebConfig.class, SecurityConfig.class}, properties = {
        "job.scheduling.enabled=true",
        "job.professional.certificates.expire.cron=0/1 0/1 * 1/1 * ? *",
})
public class CertificateExpiredTaskIT extends BaseMvcIntegrationTest {

    public static final int FOR_3_SECONDS = 3000;

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Autowired
    private SystemUserWorker systemUserWorker;

    private RegisterProfessional professional;

    @Before
    public void setUp() throws Exception {
        professional = create(RegisterProfessional.class);
        professionalWorker.registerAndActivate(professional);
    }

    @Test
    public void testExpired() throws Exception {
        professionalWorker.addSubCategories(singleton("DA"), professional);
        List<ProfessionalSubcategoryModel> professionalSubcategories = professionalWorker.listProfessionalSubCategories(professional, "");
        assertThat(professionalSubcategories.size(), Matchers.is(1));
        assertSubcategory(professionalSubcategories.get(0), "DA", "DA", "Assistants", "PENDING");

        List<RequiredCertificate> requiredCertificates = professionalWorker.listProfessionalRequiredCertificates(professional, "TYPE_ASC");

        assertThat(requiredCertificates.size(), Matchers.is(3));
        assertCertificate(requiredCertificates.get(0), true, "CPR", "PENDING", false);
        assertCertificate(requiredCertificates.get(1), true, "DAC", "PENDING", true);
        assertCertificate(requiredCertificates.get(2), true, "XRAY", "PENDING", false);

        //UPLOAD CPR
        professionalWorker.uploadCertificate(CertificateType.CPR, LocalDate.now().minusDays(1), toHttpBasic(professional));
        Thread.sleep(FOR_3_SECONDS);
        requiredCertificates = professionalWorker.listProfessionalRequiredCertificates(professional, "TYPE_ASC");
        assertThat(requiredCertificates.size(), Matchers.is(3));
        assertCertificate(requiredCertificates.get(0), false, "CPR", "EXPIRED", false);
        assertCertificate(requiredCertificates.get(1), true, "DAC", "PENDING", true);
        assertCertificate(requiredCertificates.get(2), true, "XRAY", "PENDING", false);

        //UPLOAD XRAY
        professionalWorker.uploadCertificate(CertificateType.XRAY, LocalDate.now().minusDays(1), toHttpBasic(professional));
        Thread.sleep(FOR_3_SECONDS);
        requiredCertificates = professionalWorker.listProfessionalRequiredCertificates(professional, "TYPE_ASC");
        assertThat(requiredCertificates.size(), Matchers.is(3));
        assertCertificate(requiredCertificates.get(0), false, "CPR", "EXPIRED", false);
        assertCertificate(requiredCertificates.get(1), true, "DAC", "PENDING", true);
        assertCertificate(requiredCertificates.get(2), false, "XRAY", "EXPIRED", false);

        //APPROVE UPLOADED
        systemUserWorker.approveCertificate(SYSTEM_CREDENTIALS, requiredCertificates.get(0).getCertificateId());
        systemUserWorker.approveCertificate(SYSTEM_CREDENTIALS, requiredCertificates.get(2).getCertificateId());

        //CHECK CERTIFICATES STATUS
        requiredCertificates = professionalWorker.listProfessionalRequiredCertificates(professional, "TYPE_ASC");
        assertThat(requiredCertificates.size(), Matchers.is(3));
        assertCertificate(requiredCertificates.get(0), false, "CPR", "EXPIRED", false);
        assertCertificate(requiredCertificates.get(1), true, "DAC", "PENDING", true);
        assertCertificate(requiredCertificates.get(2), false, "XRAY", "EXPIRED", false);

        //CHECK SUBCATEGORY
        professionalSubcategories = professionalWorker.listProfessionalSubCategories(professional, "");
        assertThat(professionalSubcategories.size(), Matchers.is(1));
        assertSubcategory(professionalSubcategories.get(0), "DA", "DA", "Assistants", "EXPIRED");

        //RDH LASER IS AUTO EXPIRED BECAUSE IT ONLY REQUIRES CPR
        professionalWorker.addSubCategories(singleton("RDH_LASER"), professional);
        professionalSubcategories = professionalWorker.listProfessionalSubCategories(professional, "");
        assertThat(professionalSubcategories.size(), Matchers.is(2));
        assertSubcategory(professionalSubcategories.get(0), "DA", "DA", "Assistants", "EXPIRED");
        assertSubcategory(professionalSubcategories.get(1), "RDH_LASER", "RDH with diode laser certification", "Hygienists", "EXPIRED");

        //OVERRIDE CPR
        professionalWorker.uploadCertificate(CertificateType.CPR, LocalDate.now().plusDays(5), toHttpBasic(professional));
        Thread.sleep(FOR_3_SECONDS);
        requiredCertificates = professionalWorker.listProfessionalRequiredCertificates(professional, "TYPE_ASC");
        assertThat(requiredCertificates.size(), Matchers.is(6));
        assertCertificate(requiredCertificates.get(0), false, "CPR", "REQUIRES_REVIEW", false);
        assertCertificate(requiredCertificates.get(1), true,  "DAC", "PENDING", true);
        assertCertificate(requiredCertificates.get(2), true,  "DIODE_LASER", "PENDING", true);
        assertCertificate(requiredCertificates.get(3), true,  "LIABILITY", "PENDING", false);
        assertCertificate(requiredCertificates.get(4), true,  "RDH", "PENDING", false);
        assertCertificate(requiredCertificates.get(5), false, "XRAY", "EXPIRED", false);

        professionalSubcategories = professionalWorker.listProfessionalSubCategories(professional, "");
        assertThat(professionalSubcategories.size(), Matchers.is(2));
        assertSubcategory(professionalSubcategories.get(0), "DA", "DA", "Assistants", "REQUIRES_REVIEW");
        assertSubcategory(professionalSubcategories.get(1), "RDH_LASER", "RDH with diode laser certification", "Hygienists", "REQUIRES_REVIEW");
    }

    private void assertSubcategory(ProfessionalSubcategoryModel professionalSubcategoryModel, String id, String name, String subcategoryName, String status) {
        assertThat(professionalSubcategoryModel.getId(), Matchers.is(id));
        assertThat(professionalSubcategoryModel.getSubCategoryName(), Matchers.is(name));
        assertThat(professionalSubcategoryModel.getCategoryName(), Matchers.is(subcategoryName));
        assertThat(professionalSubcategoryModel.getStatus(), Matchers.is(status));
    }

    private void assertCertificate(RequiredCertificate certificate, boolean idNull, String type, String status, boolean optional) {
        assertThat(certificate.getCertificateId(), idNull ? nullValue() : notNullValue());
        assertThat(certificate.getStatus(), Matchers.is(status));
        assertThat(certificate.getType(), Matchers.is(type));
        assertThat(certificate.isOptional(), Matchers.is(optional));
    }

}
