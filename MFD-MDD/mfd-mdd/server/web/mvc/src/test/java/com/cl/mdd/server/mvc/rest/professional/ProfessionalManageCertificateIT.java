package com.cl.mdd.server.mvc.rest.professional;

import com.cl.mdd.server.core.data.model.RegisterProfessional;
import com.cl.mdd.server.core.data.model.common.ProfessionalSubcategoryModel;
import com.cl.mdd.server.core.data.model.query.RequiredCertificate;
import com.cl.mdd.server.core.data.persistent.model.specialty.CertificateType;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.system.SystemUserWorker;
import com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hsqldb-local")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProfessionalManageCertificateIT extends BaseMvcIntegrationTest {

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
    public void testCRUD() throws Exception {
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
        professionalWorker.uploadCertificate(CertificateType.CPR, LocalDate.now().plusDays(2), toHttpBasic(professional));
        requiredCertificates = professionalWorker.listProfessionalRequiredCertificates(professional, "TYPE_ASC");
        assertThat(requiredCertificates.size(), Matchers.is(3));
        assertCertificate(requiredCertificates.get(0), false, "CPR", "REQUIRES_REVIEW", false);
        assertCertificate(requiredCertificates.get(1), true, "DAC", "PENDING", true);
        assertCertificate(requiredCertificates.get(2), true, "XRAY", "PENDING", false);

        //UPLOAD XRAY
        professionalWorker.uploadCertificate(CertificateType.XRAY, LocalDate.now().plusDays(2), toHttpBasic(professional));
        requiredCertificates = professionalWorker.listProfessionalRequiredCertificates(professional, "TYPE_ASC");
        assertThat(requiredCertificates.size(), Matchers.is(3));
        assertCertificate(requiredCertificates.get(0), false, "CPR", "REQUIRES_REVIEW", false);
        assertCertificate(requiredCertificates.get(1), true, "DAC", "PENDING", true);
        assertCertificate(requiredCertificates.get(2), false, "XRAY", "REQUIRES_REVIEW", false);


        //APPROVE UPLOADED
        systemUserWorker.approveCertificate(SYSTEM_CREDENTIALS, requiredCertificates.get(0).getCertificateId());
        systemUserWorker.approveCertificate(SYSTEM_CREDENTIALS, requiredCertificates.get(2).getCertificateId());

        //CHECK CERTIFICATES STATUS
        requiredCertificates = professionalWorker.listProfessionalRequiredCertificates(professional, "TYPE_ASC");
        assertThat(requiredCertificates.size(), Matchers.is(3));
        assertCertificate(requiredCertificates.get(0), false, "CPR", "APPROVED", false);
        assertCertificate(requiredCertificates.get(1), true, "DAC", "PENDING", true);
        assertCertificate(requiredCertificates.get(2), false, "XRAY", "APPROVED", false);

        //CHECK SUBCATEGORY
        professionalSubcategories = professionalWorker.listProfessionalSubCategories(professional, "");
        assertThat(professionalSubcategories.size(), Matchers.is(1));
        assertSubcategory(professionalSubcategories.get(0), "DA", "DA", "Assistants", "APPROVED");

        professionalWorker.addSubCategories(singleton("RDH_LASER"), professional);
        professionalSubcategories = professionalWorker.listProfessionalSubCategories(professional, "");
        assertThat(professionalSubcategories.size(), Matchers.is(2));
        assertSubcategory(professionalSubcategories.get(0), "DA", "DA", "Assistants", "APPROVED");
        assertSubcategory(professionalSubcategories.get(1), "RDH_LASER", "RDH with diode laser certification", "Hygienists", "PENDING");

        //OVERRIDE CPR
        professionalWorker.uploadCertificate(CertificateType.CPR, LocalDate.now().plusDays(5), toHttpBasic(professional));
        requiredCertificates = professionalWorker.listProfessionalRequiredCertificates(professional, "TYPE_ASC");
        assertThat(requiredCertificates.size(), Matchers.is(6));
        assertCertificate(requiredCertificates.get(0), false, "CPR", "REQUIRES_REVIEW", false);
        assertCertificate(requiredCertificates.get(1), true,  "DAC", "PENDING", true);
        assertCertificate(requiredCertificates.get(2), true,  "DIODE_LASER", "PENDING", true);
        assertCertificate(requiredCertificates.get(3), true,  "LIABILITY", "PENDING", false);
        assertCertificate(requiredCertificates.get(4), true,  "RDH", "PENDING", false);
        assertCertificate(requiredCertificates.get(5), false, "XRAY", "APPROVED", false);

        professionalSubcategories = professionalWorker.listProfessionalSubCategories(professional, "");
        assertThat(professionalSubcategories.size(), Matchers.is(2));
        assertSubcategory(professionalSubcategories.get(0), "DA", "DA", "Assistants", "REQUIRES_REVIEW");
        assertSubcategory(professionalSubcategories.get(1), "RDH_LASER", "RDH with diode laser certification", "Hygienists", "REQUIRES_REVIEW");

        //SORT SANITY CHECK
        for (String sort : Sets.newHashSet("TYPE_ASC",
                "TYPE_DESC",
                "OPTIONAL_ASC",
                "OPTIONAL_DESC")) {
            assertThat(professionalWorker.listProfessionalRequiredCertificates(professional, sort).size(), Matchers.is(6));
        }
    }

    private void assertSubcategory(ProfessionalSubcategoryModel professionalSubcategoryModel, String id, String name, String subcategoryName, String status) {
        assertThat(professionalSubcategoryModel.getId(), Matchers.is(id));
        assertThat(professionalSubcategoryModel.getSubCategoryName(), Matchers.is(name));
        assertThat(professionalSubcategoryModel.getCategoryName(), Matchers.is(subcategoryName));
        assertThat(professionalSubcategoryModel.getStatus(), Matchers.is(status));
    }

    @Test
    public void testUploadOsCertificate() throws Exception {
        MockMultipartFile osCertificate = professionalWorker.buildMockMultipartFile("", "Oral Surgery Certificate");

        String licenseNumber = "01245-663";
        String endodontist = "Endodontics";
        String oralSurgeon = "Oral surgeon";
        String expirationDate = LocalDate.now().toString();

        mockMvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/v1/upload/certificate/os")
                .file(osCertificate)
                .param("certificates[0].licenseNumber", licenseNumber)
                .param("certificates[0].expirationDate", expirationDate)
                .param("certificates[0].type", CertificateType.DAC)
                .param("speciality", oralSurgeon)
                .param("education", endodontist)
                .with(toHttpBasic(professional))
        ).andExpect(status().isOk());

    }

    @Test
    public void testAddNPICertificate() throws Exception {
        professionalWorker.addSubCategories(singleton("GENERAL_DENTIST"), professional);
        String licenseNumber = "012-896";
        mockMvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/v1/upload/certificate")
                .param("certificates[0].licenseNumber", licenseNumber)
                .param("certificates[0].type", CertificateType.NPI)
                .with(toHttpBasic(professional))
        ).andExpect(status().isOk());
    }

    @Test
    public void testAddEndodonticAssistantCertificate() throws Exception {
        professionalWorker.addSubCategories(singleton("ENDODONTIC_ASSISTANT"), professional);
        String licenseNumber = "0123456789";
        String expirationDate = LocalDate.now().toString();
        mockMvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/v1/upload/certificate")
                .param("certificates[0].licenseNumber", licenseNumber)
                .param("certificates[0].expirationDate", expirationDate)
                .param("certificates[0].type", CertificateType.ENDODONTIC_ASSISTANT)
                .with(toHttpBasic(professional))
        ).andExpect(status().isOk());
    }

    private void assertCertificate(RequiredCertificate certificate, boolean idNull, String type, String status, boolean optional) {
        assertThat(certificate.getCertificateId(), idNull ? nullValue() : notNullValue());
        assertThat(certificate.getStatus(), Matchers.is(status));
        assertThat(certificate.getType(), Matchers.is(type));
        assertThat(certificate.isOptional(), Matchers.is(optional));
    }

}
