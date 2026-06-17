package com.cl.mdd.server.core.service.payment.impl;

import com.cl.mdd.server.core.data.model.geocoding.GeocodingArea;
import com.cl.mdd.server.core.data.model.payment.*;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.persistent.access.payment.CreditCardDao;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentDao;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentMethodDao;
import com.cl.mdd.server.core.data.persistent.model.contact.Contact;
import com.cl.mdd.server.core.data.persistent.model.payment.Payment;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentAttempt;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethod;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethodCard;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import com.cl.mdd.server.core.manager.payment.KeepPaymentLocked;
import com.cl.mdd.server.core.manager.payment.PaymentAccess;
import com.cl.mdd.server.core.security.annotation.RequiresPracticeOwnerRole;
import com.cl.mdd.server.core.security.annotation.RequiresSystemUserRole;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.service.geocoding.GeocodingUtils;
import com.cl.mdd.server.core.service.notification.Notification;
import com.cl.mdd.server.core.service.notification.NotificationService;
import com.cl.mdd.server.core.service.notification.PaymentMethodCardVariables;
import com.cl.mdd.server.core.service.notification.PracticeOwnerVariables;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.cl.mdd.server.core.service.payment.PaymentInstrumentProblem;
import com.cl.mdd.server.core.service.payment.PaymentService;
import com.cl.mdd.server.core.settings.Settings;
import com.cl.mdd.server.core.settings.SystemSettings;
import com.cl.mdd.server.core.validation.group.Save;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@Validated
public class PaymentServiceImpl extends ServiceSupport implements PaymentService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String CREDIT_CARD_ABOUT_TO_EXPIRE = "CREDIT_CARD_ABOUT_TO_EXPIRE";

    @Value("${payment.3step.timeout:15}")
    private int gatewayVaultTimeout;

    // after the payment is created, there is a grace period before it is processed
    @Value("${payment.maturity.timeout:30}")
    private long paymentMaturityTimeout;

    @Value("${payment.attempt.interval:5}")
    private long paymentAttemptInterval;

    @Value("${payment.ach.check.interval:1}")
    private long paymentAchCheckInterval;

    @Value("${payment.redirect.url.vault:${mdd.domain}/api/v1/payment/vault/}")
    private String redirectUrlVault;

    @Value("${payment.redirect.url.vault:${mdd.domain}/api/v1/payment/pay/}")
    private String redirectUrlPayment;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Autowired
    private PaymentMethodCardVariables paymentMethodCardVariables;

    private final CreditCardDao creditCardDao;

    private final PaymentDao paymentDao;

    private final PaymentMethodDao paymentMethodDao;

    private final GeocodingUtils geocodingUtils;

    private final PaymentConverter paymentConverter;

    private final PaymentProcessor paymentProcessor;

    private final PaymentAccess paymentAccess;

    private final QueryConverter queryConverter;

    private final SystemSettings systemSettings;

    private final NotificationService notificationService;

    public PaymentServiceImpl(CreditCardDao creditCardDao, PaymentDao paymentDao, PaymentMethodDao paymentMethodDao,
                              GeocodingUtils geocodingUtils, PaymentConverter paymentConverter,
                              PaymentProcessor paymentProcessor, PaymentAccess paymentAccess,
                              QueryConverter queryConverter, SystemSettings systemSettings,
                              NotificationService notificationService) {
        this.creditCardDao = creditCardDao;
        this.paymentDao = paymentDao;
        this.paymentMethodDao = paymentMethodDao;
        this.geocodingUtils = geocodingUtils;
        this.paymentConverter = paymentConverter;
        this.paymentProcessor = paymentProcessor;
        this.paymentAccess = paymentAccess;
        this.queryConverter = queryConverter;
        this.systemSettings = systemSettings;
        this.notificationService = notificationService;
    }

    @RequiresPracticeOwnerRole
    public String prepareVault(String localId) {
        return paymentProcessor.urlForMethod(redirectUrlVault + localId);
    }

    @RequiresPracticeOwnerRole
    public String completeVault(String localId, String token) throws PaymentInstrumentProblem {
        PaymentMethod paymentMethod = paymentProcessor.completeVault(token);
        return executeInTransaction(() -> {
            PaymentMethod saved = paymentProcessor.save(localId, paymentMethod);
            userDao.updateLastActivityForCurrentUser();
            return saved.getId();
        });
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@paymentAccessAuthorizer.paymentMethodAccessible(#id)")
    public PaymentInstrument paymentMethod(String id) {
        return paymentConverter.toMethodModel(paymentMethodDao.findOne(id));
    }

    @Override
    @Transactional
    @PreAuthorize("@paymentAccessAuthorizer.paymentMethodAccessible(#paymentInstrument.id)")
    @Validated(Save.class)
    public PaymentInstrument save(PaymentInstrument paymentInstrument) {
        PaymentMethod paymentMethod = paymentMethodDao.findOne(paymentInstrument.getId());
        paymentMethod.setLabel(paymentInstrument.getLabel());
        if (paymentInstrument.isPreferred()) {
            paymentMethodDao.updateByPracticeSetPreferredFalse(paymentMethod.getPractice());
        }
        paymentMethod.setPreferred(paymentInstrument.isPreferred());
        PaymentMethod updated = paymentMethodDao.save(paymentMethod);
        userDao.updateLastActivityForCurrentUser();
        return paymentConverter.toMethodModel(updated);
    }

    @Override
    @PreAuthorize("@paymentAccessAuthorizer.paymentMethodAccessible(#paymentInstrumentId)")
    public void delete(String paymentInstrumentId) {
        String removedPaymentMethodVaultId = executeInTransaction(() -> {
            PaymentMethod paymentMethod = paymentMethodDao.findOne(paymentInstrumentId);
            paymentProcessor.deleteMethod(paymentMethod);
            userDao.updateLastActivityForCurrentUser();
            return paymentMethod.getVaultId();
        });
        paymentProcessor.deleteVault(removedPaymentMethodVaultId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@paymentAccessAuthorizer.currentPractice(#query?.filters?.practiceId)")
    public QueryResult<PaymentInstrument> fetchPracticePaymentMethods(PaymentMethodQuery query) {
        return queryConverter.toQueryResult(
                paymentMethodDao.findByPracticeIdAndLabelNotNull(
                        query.getFilters().getPracticeId(), queryConverter.toPageable(query.getPagination())),
                paymentConverter::toMethodModel);
    }

    @RequiresSystemUserRole
    public boolean completeTransaction(String localId, String token) {
        Payment payment = paymentProcessor.completeAttempt(localId, paymentProcessor.completeTransaction(token));
        paymentAccess.free(payment);
        return Payment.STATUS_PAID.equals(payment.getStatus());
    }

    ZonedDateTime thresholdNewPayments() {
        return ZonedDateTime.now().minusMinutes(
                systemSettings.getLong(
                        Settings.PaymentAttemptsSettings.MATURITY_MINUTES.getKey(), paymentMaturityTimeout));
    }

    LocalDateTime thresholdNewAttempts() {
        return LocalDateTime.now().minusDays(
                systemSettings.getLong(
                        Settings.PaymentAttemptsSettings.INTERVAL.getKey(), paymentAttemptInterval));
    }

    ZonedDateTime thresholdAchCheck() {
        return ZonedDateTime.now().minusDays(
                systemSettings.getLong(
                        Settings.PaymentAttemptsSettings.INTERVAL_ACH_CHECK.getKey(), paymentAchCheckInterval));
    }

    private LocalDateTime thresholdLocks() {
        return LocalDateTime.now().minusMinutes(gatewayVaultTimeout);
    }

    private ZonedDateTime thresholdInstruments() {
        return ZonedDateTime.now().minusMinutes(gatewayVaultTimeout);
    }

    public void processCurrentPayments() {
        List<Payment> payments = paymentDao.findPayments(
                this.thresholdNewPayments(), this.thresholdNewAttempts(), this.thresholdAchCheck(),
                new PageRequest(0, 100));
        for (Payment payment : payments) {
            processPayment(payment);
        }
    }

    public void processPayment(Payment payment) {
        paymentAccess.perform(payment, () -> this.process(payment));
    }

    Payment processAttempt(Payment payment, PaymentAttempt attempt, Function<PaymentAttempt, PaymentAttempt> action) {
        PaymentAttempt result = action.apply(attempt);
        if (result == null) {
            // bad attempt, need to remove
            paymentProcessor.deleteAttempt(attempt);
        } else {
            logger.debug("attempt result is {} ({})", result.getStatus(), result.getGatewayStatus());
            paymentProcessor.handleResults(result);
        }
        return payment;
    }

    Payment processAttempt(Payment payment, PaymentAttempt attempt) {
        return processAttempt(payment, attempt, paymentProcessor::processAttempt);
    }

    Payment recoverAttempt(Payment payment, PaymentAttempt attempt) {
        return processAttempt(payment, attempt, paymentProcessor::checkAttempt);
    }

    /**
     * Process a payment.
     * <p>Picks (or creates) an attempt and performs it.
     *
     * @param payment payment
     * @return processed payment or <code>null</code> if no processing performed for a failed payment
     */
    private Payment process(Payment payment) {
        PaymentAttempt attempt = paymentProcessor.pickAttempt(payment, null);
        if (attempt == null) {
            this.logger.debug("no current attempt, payment must be failed");
            return null;
        } else {
            return processAttempt(payment, attempt);
        }
    }

    @Override
    public void releasePayments() {
        List<Payment> payments = paymentDao
                .findPaymentByLockNotNullAndLockCreatedLessThan(this.thresholdLocks(), new PageRequest(0, 100));

        for (Payment payment : payments) {
            PaymentAttempt attempt = paymentProcessor.lastUnfinished(payment);
            if (attempt != null) {
                logger.debug("unfinished attempt {} for payment {}", attempt.getId(), payment.getId());
                recoverAttempt(payment, attempt);
            } else {
                logger.debug("no attempt for payment [{}], releasing the lock", payment.getId());
            }
            paymentAccess.free(payment);
        }

    }

    @Override
    @Transactional
    public void cleanupPaymentMethods() {
        ZonedDateTime threshold = thresholdInstruments();
        int number = paymentMethodDao.deleteByLabelNullAndCreatedGreaterThan(threshold);
        logger.debug("deleted {} incomplete instruments before {}", number, threshold);
        // TODO: delete also from the vault?
    }

    @Override
    @NotificationDefinition(value = CREDIT_CARD_ABOUT_TO_EXPIRE,
            predefined = {
                    PracticeOwnerVariables.class,
                    PaymentMethodCardVariables.class
            })
    public void notifyExpiringCards() {
        String expiration = LocalDate.now().plus(1, ChronoUnit.MONTHS).format(DateTimeFormatter.ofPattern("MMyy"));
        List<PaymentMethodCard> expiring = creditCardDao.findByLabelNotNullAndExpiration(expiration);

        logger.debug("sending {} notifications about credit cards expiring on {}", expiring.size(), expiration);

        expiring.forEach(paymentMethodCard -> {
            Map<String, String> context = Maps.newHashMap();

            Practice practice = paymentMethodCard.getPractice();
            PracticeOwner practiceOwner = practice.getOwner();
            Contact practiceOwnerContact = practiceOwner.getContact();

            Notification notification = new Notification();
            notification.setType(CREDIT_CARD_ABOUT_TO_EXPIRE);
            notification.setEmail(practiceOwnerContact.getEmail());
            notification.setPhone(practiceOwnerContact.getPhone());

            practiceOwnerVariables.supply(practiceOwner, context);
            paymentMethodCardVariables.supply(paymentMethodCard, context);

            notification.setContext(context);
            notificationService.send(notification);
        });
    }

    @Override
    @RequiresSystemUserRole
    @Validated
    public PaymentOptionsResponse requestManualOperation(PaymentOptionsModel request) {
        Payment payment = paymentDao.findOne(request.getPaymentId());
        String id = (payment == null ? null : payment.getId());
        logger.debug("manual operation for payment {}: {}", id, request);
        return paymentAccess.perform(payment, () -> manual(payment, request));
    }

    PaymentOptionsResponse terminalStatusAction(Payment payment, String status, String comment, BigDecimal amount) {
        paymentProcessor.commitPayment(payment, status, comment, amount);
        return PaymentOptionsResponse.done();
    }

    PaymentOptionsResponse instantPayment(Payment payment, PaymentOptionsModel request) {
        if (StringUtils.isBlank(request.getPaymentMethodId())) { // iframe etc
            PaymentAttempt attempt = paymentProcessor.externalPayment(
                    payment, request.getAmount(), request.getComment());
            class LockedResult extends PaymentOptionsResponse implements KeepPaymentLocked {
                private PaymentOptionsResponse base;

                private LockedResult(PaymentOptionsResponse base) {
                    this.base = base;
                }

                @Override
                public String getStatus() {
                    return base.getStatus();
                }

                @Override
                public String getMessage() {
                    return base.getMessage();
                }

                @Override
                public String getUrl() {
                    return base.getUrl();
                }
            }
            return new LockedResult(PaymentOptionsResponse.url(
                    paymentProcessor.urlForPayment(attempt, redirectUrlPayment + attempt.getOrderId())));
        } else { // vault
            PaymentAttempt attempt = paymentProcessor.runPayment(
                    payment, request.getAmount(), request.getPaymentMethodId(), request.getComment());
            return Payment.STATUS_PAID.equalsIgnoreCase(processAttempt(payment, attempt).getStatus())
                    ? PaymentOptionsResponse.done() : PaymentOptionsResponse.fail(attempt.getGatewayStatus());
        }
    }

    private PaymentOptionsResponse modifyAmount(Payment payment, PaymentOptionsModel request) {
        if (request.getAmount() == null || request.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive: " + request.getAmount());
        }
        paymentProcessor.updatePayment(payment, request.getAmount(), request.getComment());
        return PaymentOptionsResponse.done();
    }

    private PaymentOptionsResponse runPayment(Payment payment, PaymentOptionsModel request) {
        PaymentAttempt attempt = paymentProcessor.runPayment(
                payment, request.getAmount(), request.getPaymentMethodId(), request.getComment());
        return attempt == null
                ? PaymentOptionsResponse.fail("No new attempts allowed for this payment")
                : PaymentOptionsResponse.accepted();
    }

    private PaymentOptionsResponse manual(Payment payment, PaymentOptionsModel request) {
        switch (request.getOption()) {
            case PaymentOptionsModel.COMPLETE:
                return terminalStatusAction(payment, Payment.STATUS_PAID, request.getComment(), null);
            case PaymentOptionsModel.FAIL:
                return terminalStatusAction(payment, Payment.STATUS_FAILED, request.getComment(), null);
            case PaymentOptionsModel.CANCEL:
                return terminalStatusAction(payment, Payment.STATUS_CANCELED, request.getComment(), null);
            case PaymentOptionsModel.PARTIAL:
                return terminalStatusAction(payment, Payment.STATUS_PARTIAL, request.getComment(), request.getAmount());
            case PaymentOptionsModel.INSTANT_PAY:
                return instantPayment(payment, request);
            case PaymentOptionsModel.MODIFY:
                return modifyAmount(payment, request);
            case PaymentOptionsModel.RUN:
                return runPayment(payment, request);
            default:
                throw new MDDException("Unsupported option: " + request.getOption(), "PAYMENT_OPTION_UNSUPPORTED");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@paymentAccessAuthorizer.readAllowed(#id)")
    public PaymentModel get(String id) {
        return paymentConverter.toPaymentModel(paymentDao.findOne(id));
    }

    @Override
    @Transactional(readOnly = true)
    @RequiresSystemUserRole
    public PaymentDetails details(String id) {
        return paymentConverter.toPaymentDetails(paymentDao.findOne(id));
    }

    private QueryResult<PaymentModel> find(PaymentQuery query) {
        PaymentQuery.PaymentFilter filter = query.getFilters();
        ZonedDateTime from = filter.getFrom() == null ? null : filter.getFrom().truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime to = filter.getTo() == null ? null : filter.getTo().truncatedTo(ChronoUnit.DAYS).plusDays(1);
        GeocodingArea area = geocodingUtils.toSquaredArea(filter.getDistance(), filter.getLatitude(), filter.getLongitude());
        return queryConverter.toQueryResult(
                paymentDao.findPayments(
                        filter.getPracticeId(), filter.getStatus(), filter.getMethod(), from, to,
                        area.getDistance(), area.getLat(), area.getLng(),
                        area.getLat1(), area.getLat2(), area.getLng1(), area.getLng2(),
                        queryConverter.toPageable(query.getPagination())),
                paymentConverter::toPaymentModel);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@paymentAccessAuthorizer.currentPractice(#query?.filters?.practiceId)")
    public QueryResult<PaymentModel> fetchPracticePayments(PaymentQuery query) {
        return find(query);
    }

    @Override
    @Transactional(readOnly = true)
    @RequiresSystemUserRole
    public QueryResult<PaymentModel> fetch(PaymentQuery query) {
        return find(query);
    }
}
