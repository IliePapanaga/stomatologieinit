package com.cl.mdd.server.core.service.payment;

import com.cl.mdd.server.core.BaseIntegrationTest;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentAttemptDao;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentMethodDao;
import com.cl.mdd.server.core.data.persistent.model.common.embeddable.FullName;
import com.cl.mdd.server.core.data.persistent.model.contact.Address;
import com.cl.mdd.server.core.data.persistent.model.contact.Contact;
import com.cl.mdd.server.core.data.persistent.model.payment.Payment;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentAttempt;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethod;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.specialty.Category;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.CheckIn;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.manager.payment.PaymentManager;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.core.service.payment.impl.PaymentProcessor;
import com.cl.mdd.server.core.service.payment.impl.PaymentServiceImpl;
import com.cl.mdd.server.core.service.payment.impl.primerate.Credentials;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

@ContextConfiguration(classes = { WebClientAutoConfiguration.class })
@AutoConfigureDataJpa
@ActiveProfiles("mock-notification-service")
@WithMockUser
public class PaymentFlowIT extends BaseIntegrationTest {

    private long seed = System.currentTimeMillis();

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private PaymentAttemptDao paymentAttemptDao;

    @Autowired
    private PaymentMethodDao paymentMethodDao;

    @Autowired
    private PaymentProcessor paymentProcessor;

    @Autowired
    private PaymentManager paymentManager;

    @Autowired
    private PaymentService paymentService;

    private Practice practice;

    private JobDay jobDay;

    private CheckIn checkIn;

    @Before
    public void setUp() {
        transactionHelper.executeInTransaction(() -> {
            Contact contact = new Contact();
            contact.setName(new FullName());
            contact.getName().setFirst(seed + " f");
            contact.getName().setLast(seed + " l");
            contact.setEmail(seed + "@" + this.getClass().getName());
            contact.setPhone(Long.toString(seed));
            contact.setAddress(new Address());
            contact.getAddress().setCity(seed + " city");
            contact.getAddress().setState(seed + " state");
            contact.getAddress().setStreet(seed + " street");
            contact.getAddress().setZipCode(Long.toString(seed));
            PracticeOwner practiceOwner = new PracticeOwner();
            practiceOwner.setContact(contact);
            practiceOwner.setStatus("ACTIVE");
            practiceOwner.setUsername(seed + "login");
            practice = new Practice();
            practice.setName(seed + "test-flow");
            practice.setOwner(practiceOwner);
            practice.setBillingAddress(new Address());
            practice.getBillingAddress().setCity(seed + " city");
            practice.getBillingAddress().setState(seed + " state");
            practice.getBillingAddress().setStreet(seed + " street");
            practice.getBillingAddress().setZipCode(Long.toString(seed));
            PracticeLocation location = new PracticeLocation();
            location.setContact(contact);
            location.setName(seed + "office");
            location.setPractice(practice);
            location.setTimeZone(ZoneId.of(ZoneId.SHORT_IDS.get("PST")));
            entityManager.persist(practiceOwner);
            entityManager.persist(practice);
            entityManager.persist(location);

            Category specialty = new Category();
            specialty.setName(seed + " specialty");
            SubCategory jobTitle = new SubCategory();
            jobTitle.setName(seed + " sub type");
            jobTitle.setCategory(specialty);
            entityManager.persist(specialty);
            entityManager.persist(jobTitle);

            TemporaryJobPosting posting = new TemporaryJobPosting();
            posting.setComment(seed + " comment");
            posting.setEndTime(LocalTime.NOON.plusHours(5L));
            posting.setEndDate(LocalDate.now().plusDays(5L));
            posting.setLocation(location);
            posting.setName(seed + " test posting");
            posting.setStartDate(LocalDate.now().plusMonths(1L));
            posting.setStartTime(LocalTime.NOON.minusHours(3L));
            posting.setStatus("ACTIVE");
            posting.setSubCategories(Collections.singleton(jobTitle));
            entityManager.persist(posting);

            jobDay = new JobDay();
            jobDay.setDate(LocalDate.now());
            jobDay.setEndTime(LocalTime.NOON.minusHours(3L));
            jobDay.setJobPosting(posting);
            jobDay.setStartTime(LocalTime.NOON.plusHours(5L));
            entityManager.persist(jobDay);

            Professional pro = new Professional();
            pro.setStatus(User.ACTIVE);
            pro.setUsername(seed + "_login");
            pro.setContact(new Contact());
            pro.getContact().setEmail(seed + "m@i.l");
            pro.getContact().setPhone(seed + "");
            pro.getContact().setAddress(new Address());
            pro.getContact().getAddress().setCity("city");
            pro.getContact().getAddress().setState("state");
            pro.getContact().getAddress().setStreet("street");
            pro.getContact().getAddress().setZipCode("00000");
            pro.getContact().setName(new FullName());
            pro.getContact().getName().setFirst("f");
            pro.getContact().getName().setLast("l");
            entityManager.persist(pro);

            TemporaryJobPostingApplication application = new TemporaryJobPostingApplication();
            application.setJobDays(Collections.singleton(jobDay));
            application.setTemporaryJobPosting(posting);
            application.setProfessional(pro);
            application.setStatus(TemporaryJobPostingApplication.ACCEPTED);
            entityManager.persist(application);

            checkIn = new CheckIn();
            checkIn.setJobDay(jobDay);
            checkIn.setProfessional(pro);

            entityManager.persist(checkIn);

            entityManager.flush();
        });
    }

    private void delete(PaymentMethod paymentMethod) {
        String key = ((Credentials) ReflectionTestUtils.invokeMethod(paymentProcessor, "credentials")).getApiKey();
        HttpPost post = new HttpPost("https://primeratemerchant.transactiongateway.com/api/v2/three-step");
        post.setHeader("Content-Type", ContentType.TEXT_XML.getMimeType());
        try {
            post.setEntity(new StringEntity(
                    "<delete-customer><api-key>" + key + "</api-key><customer-vault-id>"
                    + paymentMethod.getVaultId() + "</customer-vault-id></delete-customer>"));
//            HttpResponse response = httpClient.execute(post);
//            System.out.println(IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
//            <response><result>1</result><result-text>Customer Deleted</result-text><result-code>100</result-code></response>
            httpClient.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        // cleanup the vault
        transactionHelper.executeInTransaction(() ->
                paymentMethodDao.findByPracticeAndLabelNotNullOrderByPreferredDesc(practice).forEach(this::delete));
    }

    private HttpResponse postCreditCard(String submitUrl) throws IOException {
        HttpPost post = new HttpPost(submitUrl);
        post.setHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
        post.setEntity(new StringEntity(
                "billing-cc-number=4111111111111111&billing-cc-exp=10" + (LocalDateTime.now().getYear() - 2000 + 10)));
        return httpClient.execute(post);
    }

    private Payment payment() {
        paymentManager.payTemporary(jobDay.getId());
        return (Payment) entityManager
                .createQuery("from " + Payment.class.getName() + " p where p.jobDay=:day")
                .setParameter("day", jobDay)
                .getResultList().iterator().next();
    }

    @Test
    public void testVaultFlow() throws Exception {
        // prepare form
        String submitUrl = paymentService.prepareVault(practice.getId());
        // save card
        HttpResponse saveResponse = postCreditCard(submitUrl);
        // get token
        String token = StringUtils.substringAfter(saveResponse.getFirstHeader("Location").getValue(), "?token-id=");
        // complete save to vault
        paymentService.completeVault(practice.getId(), token);
        transactionHelper.executeInTransaction(() ->
                entityManager
                        .createQuery("select m from PaymentMethod m where m.practice=:p", PaymentMethod.class)
                        .setParameter("p", practice)
                        .getResultList().forEach(m -> {
                            m.setLabel("test" + seed);
                            entityManager.merge(m);
                        }));
        // pay
        Payment payment = payment();
        ((PaymentServiceImpl) paymentService).processPayment(payment);
        // assert
        transactionHelper.executeInTransaction(() -> {
            Payment p = entityManager.find(Payment.class, payment.getId());
            Assert.assertEquals(Payment.STATUS_PAID, p.getStatus());
        });
        // check payment @ gateway
        PaymentAttempt testAttempt = new PaymentAttempt();
        testAttempt.setOrderId(paymentAttemptDao.findByPaymentOrderByCreatedDesc(payment).get(0).getOrderId());
        PaymentAttempt resultAttempt = paymentProcessor.processAttempt(testAttempt);
        Assert.assertEquals(PaymentAttempt.STATUS_PAID, resultAttempt.getStatus());
        Assert.assertNotNull(resultAttempt.getGatewayId());
    }

    @Test
    public void testManualFlow() throws Exception {
        // prepare
        Payment payment = payment();
        transactionHelper.executeInTransaction(() -> {
            PaymentAttempt attempt = new PaymentAttempt();
            attempt.setPayment(payment);
            attempt.setAmount(payment.getAmount());
            paymentAttemptDao.save(attempt);
        });
        PaymentAttempt attempt = paymentAttemptDao.findByPaymentOrderByCreatedDesc(payment).get(0);
        // get URL
        String url = paymentProcessor.urlForPayment(attempt, "callback/order-id");
        // submit card data
        HttpResponse saveResponse = postCreditCard(url);
        // get token
        String token = StringUtils.substringAfter(saveResponse.getFirstHeader("Location").getValue(), "?token-id=");
        // complete
        paymentService.completeTransaction(attempt.getOrderId(), token);
        // verify
        transactionHelper.executeInTransaction(() -> {
            Payment p = entityManager.find(Payment.class, payment.getId());
            Assert.assertEquals(Payment.STATUS_PAID, p.getStatus());
        });
    }

    @Test
    public void testExpirationJob() throws Exception {
        testVaultFlow();
        transactionHelper.executeInTransaction(() ->
            entityManager
                    .createQuery("update PaymentMethod m set m.expiration=:exp where m.practice=:p")
                    .setParameter("p", practice)
                    .setParameter("exp", LocalDate.now().plus(1, ChronoUnit.MONTHS)
                            .format(DateTimeFormatter.ofPattern("MMyy")))
                    .executeUpdate()
        );
        paymentService.notifyExpiringCards();
    }
}
