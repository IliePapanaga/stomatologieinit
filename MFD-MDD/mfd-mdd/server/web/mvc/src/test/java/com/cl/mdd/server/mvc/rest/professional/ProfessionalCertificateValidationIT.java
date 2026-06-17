package com.cl.mdd.server.mvc.rest.professional;

import com.cl.mdd.server.core.data.model.RegisterProfessional;
import com.cl.mdd.server.core.data.model.errors.ErrorInfoModel;
import com.cl.mdd.server.core.data.persistent.model.specialty.CertificateType;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collection;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.collectionOf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hsqldb-local")
public class ProfessionalCertificateValidationIT extends BaseMvcIntegrationTest {

    @Autowired
    private ProfessionalWorker professionalWorker;

    private RegisterProfessional professional;

    @Before
    public void setUp() throws Exception {
        professional = create(RegisterProfessional.class);
        professionalWorker.registerAndActivate(professional);
    }

    @Test
    public void testValidateXRAYCertificate() throws Exception {
        MvcResult result = uploadEmptyCertificateMvc(CertificateType.XRAY);
        Collection<ErrorInfoModel> errors = collectionOf(result.getResponse().getContentAsString(), ErrorInfoModel.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("X-Ray License should be specified.")));
    }

    @Test
    public void testValidateCPRCertificate() throws Exception {
        MvcResult result = uploadEmptyCertificateMvc(CertificateType.CPR);
        Collection<ErrorInfoModel> errors = collectionOf(result.getResponse().getContentAsString(), ErrorInfoModel.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Cardio Pulmonary Resuscitation(CPR) expiration date should be specified.")));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Cardio Pulmonary Resuscitation(CPR) certificate should be specified.")));
    }

    @Test
    public void testValidateRDACertificate() throws Exception {
        MvcResult result = uploadEmptyCertificateMvc(CertificateType.RDA);
        Collection<ErrorInfoModel> errors = collectionOf(result.getResponse().getContentAsString(), ErrorInfoModel.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("RDA or RDAEF or RDAEF 1 or RDAEF 2 certificate should be specified.")));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("RDA or RDAEF or RDAEF 1 or RDAEF 2 number should be specified.")));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("RDA or RDAEF or RDAEF 1 or RDAEF 2 expiration date should be specified.")));
    }

    @Test
    public void testValidateRDHCertificate() throws Exception {
        MvcResult result = uploadEmptyCertificateMvc(CertificateType.RDH);
        Collection<ErrorInfoModel> errors = collectionOf(result.getResponse().getContentAsString(), ErrorInfoModel.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("RDH or RDHAP License certificate should be specified.")));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("RDH or RDHAP number entry should be specified.")));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("RDH or RDHAP expiration date should be specified.")));
    }

    @Test
    public void testValidateLiabilityCertificate() throws Exception {
        MvcResult result = uploadEmptyCertificateMvc(CertificateType.LIABILITY);
        Collection<ErrorInfoModel> errors = collectionOf(result.getResponse().getContentAsString(), ErrorInfoModel.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Liability insurance certificate should be specified.")));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Liability insurance expiration date should be specified.")));
    }

    @Test
    public void testValidateDDSCertificate() throws Exception {
        MvcResult result = uploadEmptyCertificateMvc(CertificateType.DDS_OR_DMD);
        Collection<ErrorInfoModel> errors = collectionOf(result.getResponse().getContentAsString(), ErrorInfoModel.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("DDS or DMD license file should be specified.")));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("DDS or DMD license number should be specified.")));
    }

    @Test
    public void testValidateDEACertificate() throws Exception {
        MvcResult result = uploadEmptyCertificateMvc(CertificateType.DEA);
        Collection<ErrorInfoModel> errors = collectionOf(result.getResponse().getContentAsString(), ErrorInfoModel.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("DEA license file should be specified.")));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("DEA license number should be specified.")));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("DEA license expiration date should be specified.")));
    }

    @Test
    public void testValidateNPICertificate() throws Exception {
        MvcResult result = uploadEmptyCertificateMvc(CertificateType.NPI);
        Collection<ErrorInfoModel> errors = collectionOf(result.getResponse().getContentAsString(), ErrorInfoModel.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("National Identification Number (NPI) should be specified.")));
    }

    @Test
    public void testValidateEndodonticAssistantCertificate() throws Exception {
        MvcResult result = uploadEmptyCertificateMvc(CertificateType.ENDODONTIC_ASSISTANT);
        Collection<ErrorInfoModel> errors = collectionOf(result.getResponse().getContentAsString(), ErrorInfoModel.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Endodontic assistant license number should be specified.")));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Endodontic assistant license expiration date should be specified.")));
    }

    @Test
    public void testValidateOralSurgeryAssistantCertificate() throws Exception {
        MvcResult result = uploadEmptyCertificateMvc(CertificateType.ORAL_SURGERY_ASSISTANT);
        Collection<ErrorInfoModel> errors = collectionOf(result.getResponse().getContentAsString(), ErrorInfoModel.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Oral surgery assistant license number should be specified.")));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Oral surgery assistant license expiration date should be specified.")));
    }

    @Test
    public void testValidateOrthodonticAssistantCertificate() throws Exception {
        MvcResult result = uploadEmptyCertificateMvc(CertificateType.ORTHODONTIC_ASSISTANT);
        Collection<ErrorInfoModel> errors = collectionOf(result.getResponse().getContentAsString(), ErrorInfoModel.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Orthodontic assistant license number should be specified.")));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Orthodontic assistant license expiration date should be specified.")));
    }

    @Test
    public void testValidatePedodonticAssistantCertificate() throws Exception {
        MvcResult result = uploadEmptyCertificateMvc(CertificateType.PEDODONTIC_ASSISTANT);
        Collection<ErrorInfoModel> errors = collectionOf(result.getResponse().getContentAsString(), ErrorInfoModel.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Pedodontic assistant license number should be specified.")));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Pedodontic assistant license expiration date should be specified.")));
    }

    @Test
    public void testValidatePeriodontalAssistantCertificate() throws Exception {
        MvcResult result = uploadEmptyCertificateMvc(CertificateType.PERIODONTAL_ASSISTANT);
        Collection<ErrorInfoModel> errors = collectionOf(result.getResponse().getContentAsString(), ErrorInfoModel.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Periodontal assistant license number should be specified.")));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Periodontal assistant license expiration date should be specified.")));
    }

    @Test
    public void testValidateInvalidCertificate() throws Exception {
        MvcResult result = uploadEmptyCertificateMvc("INVALID");
        Collection<ErrorInfoModel> errors = collectionOf(result.getResponse().getContentAsString(), ErrorInfoModel.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getMessage().equals("Unsupported certificate type.")));
    }

    private MvcResult uploadEmptyCertificateMvc(String type) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/v1/upload/certificate")
                .param("certificates[0].type", type)
                .with(toHttpBasic(professional))
        ).andExpect(status().isBadRequest()).andReturn();
    }

}
