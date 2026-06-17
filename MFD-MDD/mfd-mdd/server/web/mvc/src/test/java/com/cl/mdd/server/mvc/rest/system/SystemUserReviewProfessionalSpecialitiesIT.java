package com.cl.mdd.server.mvc.rest.system;

import com.cl.mdd.server.core.data.model.ErrorAssert;
import com.cl.mdd.server.core.data.model.ProfessionalModel;
import com.cl.mdd.server.core.data.model.RegisterProfessional;
import com.cl.mdd.server.core.data.model.RejectCertificateDetailsModel;
import com.cl.mdd.server.core.data.model.certificates.CertificateDetailsModel;
import com.cl.mdd.server.core.data.model.common.ProfessionalSubcategoryModel;
import com.cl.mdd.server.core.data.model.query.RequiredCertificate;
import com.cl.mdd.server.core.data.persistent.model.specialty.CertificateType;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import com.cl.mdd.server.core.service.user.ProfessionalSubcategoryService;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.GraphQLRequestRepository;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"hsqldb-local"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SystemUserReviewProfessionalSpecialitiesIT extends BaseMvcIntegrationTest {

    @Autowired
    private SystemUserWorker systemUserWorker;
    @Autowired
    private ProfessionalWorker professionalWorker;
    @Autowired
    private ProfessionalSubcategoryService subcategoryService;

    private RegisterProfessional professional;
    private ProfessionalModel registeredProfessional;

    @Before
    public void setUp() throws Exception {
        professional = create(RegisterProfessional.class);
        registeredProfessional = professionalWorker.registerAndActivate(professional);
        professionalWorker.addSubCategories(new HashSet<>(Arrays.asList("RDA", "DA")), professional);
        uploadRdaSpecialityCertificates(LocalDate.now().plusDays(1));
    }

    private void uploadRdaSpecialityCertificates(LocalDate time) throws Exception {
        MockMultipartFile rdaCertificate = buildMockMultipartFile("certificates[0].file", "rda.txt", "RDA Content");
        MockMultipartFile cprCertificate = buildMockMultipartFile("certificates[1].file", "cpr.txt", "CPR Content");
        MockMultipartFile cdaCertificate = buildMockMultipartFile("certificates[2].file", "cda.txt", "CDA Content");
        mockMvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/v1/upload/certificate")
                .file(rdaCertificate)
                .file(cprCertificate)
                .file(cdaCertificate)
                .param("certificates[0].type", CertificateType.RDA)
                .param("certificates[0].expirationDate", time.toString())
                .param("certificates[0].licenseNumber", "012-963")
                .param("certificates[0].subcategoryId", "RDA")
                .param("certificates[1].type", CertificateType.CPR)
                .param("certificates[1].expirationDate", time.toString())
                .param("certificates[1].subcategoryId", "RDA")
                .param("certificates[2].type", CertificateType.CDA)
                .param("certificates[2].expirationDate", time.toString())
                .param("certificates[2].subcategoryId", "RDA")
                .with(toHttpBasic(professional))
        ).andExpect(status().isOk());
    }

    private void uploadDaSpecialityCertificates(LocalDate time) throws Exception {
        MockMultipartFile cprCertificate = buildMockMultipartFile("certificates[0].file", "cpr.txt", "CPR Content");
        MockMultipartFile xrayCertificate = buildMockMultipartFile("certificates[1].file", "xray.txt", "xRay Content");
        mockMvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/v1/upload/certificate")
                .file(cprCertificate)
                .file(xrayCertificate)
                .param("certificates[0].type", CertificateType.CPR)
                .param("certificates[0].expirationDate", time.toString())
                .param("certificates[0].subcategoryId", "DA")
                .param("certificates[1].type", CertificateType.XRAY)
                .param("certificates[1].subcategoryId", "DA")
                .with(toHttpBasic(professional))
        ).andExpect(status().isOk());
    }

    private MockMultipartFile buildMockMultipartFile(String fieldName, String fileName, String fileContent) throws IOException {
        InputStream certificate = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8.name()));
        return new MockMultipartFile(fieldName, fileName, "text/plain; charset=UTF-8", certificate);
    }

    @Test
    public void testRejectProfessionalCertificate() throws Exception {
        List<RequiredCertificate> certificates = professionalWorker.listProfessionalRequiredCertificates("", SYSTEM_CREDENTIALS, registeredProfessional.getId());
        RequiredCertificate rda = certificates.stream().filter(requiredCertificate -> requiredCertificate.getType().equals("RDA")).findFirst().get();
        RejectCertificateDetailsModel rejectModel = new RejectCertificateDetailsModel().setId(rda.getCertificateId()).setComment("comment");

        systemUserWorker.rejectCertificate(SYSTEM_CREDENTIALS, rejectModel);

        CertificateDetailsModel certificate = systemUserWorker.certificate(SYSTEM_CREDENTIALS, rda.getCertificateId());
        List<ProfessionalSubcategoryModel> subCategories = professionalWorker.listProfessionalSubCategories(professional, "");
        ProfessionalSubcategoryModel rdaSubcategory = subCategories.stream().filter(p -> p.getId().equals("RDA")).findFirst().get();
        Assert.assertEquals(certificate.getStatus(), CertificateDetails.REJECTED);
        Assert.assertEquals(certificate.getComment(), rejectModel.getComment());
        Assert.assertEquals(rdaSubcategory.getStatus(), SubCategory.REQUIRES_REVIEW);
    }

    @Test
    public void testValidateApproveProfessionalSpeciality() throws Exception {
        List<RequiredCertificate> certificates = professionalWorker.listProfessionalRequiredCertificates("", SYSTEM_CREDENTIALS, registeredProfessional.getId());
        for (RequiredCertificate certificate : certificates) {
            String certificateId = certificate.getCertificateId();
            if (certificateId != null) {
                systemUserWorker.approveCertificate(SYSTEM_CREDENTIALS, certificateId);
            }
        }
        List<ProfessionalSubcategoryModel> subCategories = professionalWorker.listProfessionalSubCategories(professional, "");
        ProfessionalSubcategoryModel rdaSubcategory = subCategories.stream().filter(p -> p.getId().equals("RDA")).findAny().get();
        Assert.assertEquals(rdaSubcategory.getStatus(), SubCategory.APPROVED);
    }

    @Test
    public void testValidatePendingProfessionalSpeciality() throws Exception {
        List<RequiredCertificate> certificates = professionalWorker.listProfessionalRequiredCertificates("", SYSTEM_CREDENTIALS, registeredProfessional.getId());
        RequiredCertificate rda = certificates.stream().filter(requiredCertificate -> requiredCertificate.getType().equals("RDA")).findFirst().get();

        systemUserWorker.approveCertificate(SYSTEM_CREDENTIALS, rda.getCertificateId());

        List<ProfessionalSubcategoryModel> subCategories = professionalWorker.listProfessionalSubCategories(professional, "");
        Assert.assertTrue(subCategories.stream().allMatch(subcategory -> subcategory.getStatus().equals(CertificateDetails.REQUIRES_REVIEW)));
    }

    @Test
    public void testValidateRejectCertificateDetailsRequest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.rejectCertificate(new RejectCertificateDetailsModel());
        requestBuilder.with(SYSTEM_CREDENTIALS);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", not(empty()))
                ).andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Certificate Details id should be specified.",  "rejectCertificateDetails.model.id")
                .andExpect("Reject Certificate Details comment should be specified.",  "rejectCertificateDetails.model.comment");
    }

    @Test
    public void testValidateSpecialityWithNotMandatoryExpiredCertificate() throws Exception {
        // set CDA certificateDetails of Assistant speciality to expired status
        MockMultipartFile cdaCertificate = buildMockMultipartFile("certificates[0].file", "cda.txt", "CDA Content");
        mockMvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/v1/upload/certificate")
                .file(cdaCertificate)
                .param("certificates[0].type", CertificateType.CDA)
                .param("certificates[0].expirationDate", LocalDate.now().minusDays(1).toString())
                .param("certificates[0].licenseNumber", "012-963")
                .param("certificates[0].subcategoryId", "RDA")
                .with(toHttpBasic(professional))
        ).andExpect(status().isOk());

        List<RequiredCertificate> certificates = professionalWorker.listProfessionalRequiredCertificates("", SYSTEM_CREDENTIALS, registeredProfessional.getId());
        for (RequiredCertificate certificate : certificates) {
            String certificateId = certificate.getCertificateId();
            if (certificateId != null) {
                systemUserWorker.approveCertificate(SYSTEM_CREDENTIALS, certificateId);
            }
        }

        List<ProfessionalSubcategoryModel> result = professionalWorker.listProfessionalSubCategories(professional, "");
        ProfessionalSubcategoryModel dbRdaSubcategory = result.stream().filter(p -> p.getId().equals(CertificateType.RDA)).findAny().get();
        Assert.assertEquals(dbRdaSubcategory.getStatus(), SubCategory.APPROVED);
    }

    @Test
    public void testApproveSpecialityWithRejectedNotMandatoryCertificate() throws Exception {
        // approve all professional certificates
        List<RequiredCertificate> certificates = professionalWorker.listProfessionalRequiredCertificates("", SYSTEM_CREDENTIALS, registeredProfessional.getId());
        for (RequiredCertificate certificate : certificates) {
            String certificateId = certificate.getCertificateId();
            if (certificateId != null) {
                systemUserWorker.approveCertificate(SYSTEM_CREDENTIALS, certificateId);
            }
        }

        // reject not mandatory CDA certificateDetails
        RequiredCertificate requiredCertificate = certificates.stream().filter(p -> p.getType().equals(CertificateType.CDA)).findAny().get();
        systemUserWorker.rejectCertificate(SYSTEM_CREDENTIALS, new RejectCertificateDetailsModel().setId(requiredCertificate.getCertificateId()).setComment("rejected"));

        List<ProfessionalSubcategoryModel> result = professionalWorker.listProfessionalSubCategories(professional, "");
        ProfessionalSubcategoryModel dbRdaSubcategory = result.stream().filter(p -> p.getId().equals(CertificateType.RDA)).findAny().get();
        Assert.assertEquals(dbRdaSubcategory.getStatus(), SubCategory.APPROVED);
    }

    @Test
    public void testListProfessionalSpecialitiesByProfessionalId() throws Exception {
        List<ProfessionalSubcategoryModel> result = systemUserWorker.listProfessionalSubCategoriesByProfessionalId(SYSTEM_CREDENTIALS, registeredProfessional.getId());
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void testListProfessionalSpecialitiesByFaikeProfessionalId() throws Exception {
        List<ProfessionalSubcategoryModel> result = systemUserWorker.listProfessionalSubCategoriesByProfessionalId(SYSTEM_CREDENTIALS, "1234");
        Assert.assertTrue(result.isEmpty());
    }
}
