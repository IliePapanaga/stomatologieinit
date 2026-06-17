package com.cl.mdd.server.mvc.rest;

import com.cl.mdd.server.core.data.model.RegisterProfessional;
import com.cl.mdd.server.core.data.model.RegisterUser;
import com.cl.mdd.server.core.data.model.common.AddressModel;
import com.cl.mdd.server.core.data.model.factory.ModelFactoryRegistry;
import com.cl.mdd.server.core.data.model.query.RequiredCertificate;
import com.cl.mdd.server.core.event.EventCleanupHandler;
import com.cl.mdd.server.core.event.impl.bus.SignUpCompletedEventBus;
import com.cl.mdd.server.core.event.impl.handler.SignUpCompletedEventHandler;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.core.service.notification.client.NotificationServiceClient;
import com.cl.mdd.server.mvc.config.SecurityConfig;
import com.cl.mdd.server.mvc.config.WebConfig;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import com.cl.mdd.server.mvc.rest.system.SystemUserWorker;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singleton;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Matchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
//@DataJpaTest start
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@OverrideAutoConfiguration(enabled = false)
//@TypeExcludeFilters(DataJpaTypeExcludeFilter.class)
@AutoConfigureCache
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@ImportAutoConfiguration
//@DataJpaTest end
@SpringBootTest(classes = {WebConfig.class, SecurityConfig.class})
@EnableWebMvc
@AutoConfigureMockMvc
@AutoConfigureWebClient(registerRestTemplate = true)
@JsonTest
@ComponentScans(value = {
        @ComponentScan("com.cl.mdd.server.mvc"),
        @ComponentScan("com.cl.mdd.server.core.data.model.factory")
})
@Import({ValidationAutoConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseMvcIntegrationTest {
    public static final String SECURED_API_PATH = "/api/v1/graphql";
    public static final String API_PATH = SECURED_API_PATH + "/public";

    @Autowired
    protected ProfessionalWorker professionalWorker;

    @Autowired
    protected SystemUserWorker systemUserWorker;

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected NotificationServiceClient notificationServiceClient;

    @Autowired
    private TransactionHelper transactionHelper;

    @SpyBean
    private SignUpCompletedEventBus SignUpCompletedEventBus;

    @MockBean
    private EventCleanupHandler eventCleanupHandler;

    @Autowired
    private ModelFactoryRegistry modelFactoryRegistry;

    @Autowired
    private SignUpCompletedEventBus autowiredSignUpCompletedEventBus;

    @Before
    public void setUpRoot() throws Exception {
        Mockito.doNothing().when(autowiredSignUpCompletedEventBus).publishEvent(any());
    }

    public static RequestPostProcessor SYSTEM_CREDENTIALS = httpBasic("iana@mdd.com", "QAZws12345");

    public static final double WHITE_HOUSE_LAT = 38.8976763;

    public static final double WHITE_HOUSE_LNG = -77.0365298;

    public static final double ORIGIN_LAT = 47.03736;

    public static final double ORIGIN_LNG = 28.814521;

    public static final double BUCHAREST_LAT = 44.4266713;

    public static final double BUCHAREST_LNG = 26.0946592;

    public static final double BALTI_LAT = 47.758924;

    public static final double BALTI_LNG = 27.926588;

    public static final double CHISINAU_LAT = 47.0105;

    public static final double CHISINAU_LNG = 28.8638;

    public static final double PARIS_LAT = 48.86529;

    public static final double PARIS_LNG = 2.33434;

    public static AddressModel BUCHAREST_ADDRESS = new AddressModel() {
        {
            this.setCity("București");
            this.setState(randomAlphabetic(10));
            this.setZipCode("40106");
            this.setStreet("Bloc 6, Bulevardul Unirii 14");
            this.setCountry("Romania");
            this.setLatitude(BUCHAREST_LAT);
            this.setLongitude(BUCHAREST_LNG);
        }
    };

    public static AddressModel PARIS_ADDRESS = new AddressModel() {
        {
            this.setCity("Paris");
            this.setState(randomAlphabetic(10));
            this.setZipCode("75007");
            this.setStreet("Champ de Mars, 2 Allée Adrienne Lecouvreur");
            this.setCountry("France");
            this.setLatitude(PARIS_LAT);
            this.setLongitude(PARIS_LNG);
        }
    };

    public static AddressModel WASHINGTON_ADDRESS = new AddressModel() {
        {
            this.setState(randomAlphabetic(10));
            this.setCity("Washington");
            this.setZipCode("20500");
            this.setStreet("The White House, 1600 Pennsylvania Ave NW");
            this.setCountry("USA");
            this.setLatitude(WHITE_HOUSE_LAT);
            this.setLongitude(WHITE_HOUSE_LNG);
        }
    };

    public static RequestPostProcessor toHttpBasic(RegisterUser user) {
        return httpBasic(user.getUsername(), user.getPassword());
    }

    public <T> T create(Class<T> type) {
        return modelFactoryRegistry.create(type);
    }

    protected void assertAccessDeniedFor(MockHttpServletRequestBuilder with) throws Exception {
        mockMvc.perform(with).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors[0].message", containsString("Access is denied")));
    }


    @TestConfiguration
    @Profile("USE_SIGN_UP_COMPLETED_EVENT_BUS")
    public static class SignUpCompletedEventBusTestConfiguration {

        @SpyBean
        private SignUpCompletedEventHandler signUpCompletedEventHandler;

    }

//    public static void invokeVoidMethod(InvocationOnMock invocation) throws Exception {
//        invocation.getMethod().invoke(invocation.getMock(), invocation.getArguments());
//    }

    public static void invokeVoidMethod(InvocationOnMock invocation, Object on) throws Exception {
        invocation.getMethod().invoke(on, invocation.getArguments());
    }

    protected void addAndApproveRDA(RegisterProfessional proAccount) throws Exception {
        professionalWorker.addSubCategories(singleton("RDA"), proAccount);
        professionalWorker.uploadCertificate("CPR", LocalDate.now().plusYears(1), toHttpBasic(proAccount));
        professionalWorker.uploadCertificate("RDA", LocalDate.now().plusYears(1), toHttpBasic(proAccount));

        List<RequiredCertificate> certificates = professionalWorker.listProfessionalRequiredCertificates(proAccount, "TYPE_ASC");
        systemUserWorker.approveCertificate(SYSTEM_CREDENTIALS, certificates.get(1).getCertificateId());
        systemUserWorker.approveCertificate(SYSTEM_CREDENTIALS, certificates.get(2).getCertificateId());
    }

}
