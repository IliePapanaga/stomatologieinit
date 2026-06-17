package com.cl.mdd.server.core.service.payment.impl;

import com.cl.mdd.server.core.data.persistent.access.payment.PaymentAttemptDao;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentDao;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentMethodDao;
import com.cl.mdd.server.core.data.persistent.access.practice.PracticeDao;
import com.cl.mdd.server.core.data.persistent.model.payment.*;
import com.cl.mdd.server.core.event.impl.bus.payment.PaymentStatusEventBus;
import com.cl.mdd.server.core.event.type.payment.PaymentStatusEvent;
import com.cl.mdd.server.core.service.payment.PaymentInstrumentProblem;
import com.cl.mdd.server.core.service.payment.impl.primerate.Credentials;
import com.cl.mdd.server.core.service.payment.impl.primerate.GatewayClient;
import com.cl.mdd.server.core.service.payment.impl.primerate.Result;
import com.cl.mdd.server.core.service.payment.impl.primerate.model.Billing;
import com.cl.mdd.server.core.service.payment.impl.primerate.model.EndResponse;
import com.cl.mdd.server.core.service.payment.impl.primerate.model.VaultResponse;
import com.cl.mdd.server.core.settings.Settings;
import com.cl.mdd.server.core.settings.SystemSettings;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * (Internal) Payment operations.
 * <p/>
 * Basically, all payment operations are implemented here.
 * The methods are for service layer convenience. They also
 * hide the {@link GatewayClient payment gateway} interactions.
 */
@Component
public class PaymentProcessor {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private final PaymentAttemptDao paymentAttemptDao;
    private final PaymentMethodDao paymentMethodDao;
    private final PaymentDao paymentDao;
    private final PracticeDao practiceDao;
    private final GatewayClient gateway;
    private final SystemSettings systemSettings;
    private final PaymentStatusEventBus paymentStatusEventBus;

    @Autowired
    private PaymentProcessor self;

    private static final List<String> PAYMENT_STATUSES_FOR_NOTIFICATION = Collections.unmodifiableList(Arrays.asList(
            PaymentAttempt.STATUS_FAILED, PaymentAttempt.STATUS_PAID));

    public PaymentProcessor(
            PaymentAttemptDao paymentAttemptDao, PaymentMethodDao paymentMethodDao, PaymentDao paymentDao, PracticeDao practiceDao,
            GatewayClient gateway, SystemSettings systemSettings, PaymentStatusEventBus paymentStatusEventBus) {
        this.paymentAttemptDao = paymentAttemptDao;
        this.paymentMethodDao = paymentMethodDao;
        this.paymentDao = paymentDao;
        this.practiceDao = practiceDao;
        this.gateway = gateway;
        this.systemSettings = systemSettings;
        this.paymentStatusEventBus = paymentStatusEventBus;
    }

    String property(Settings.Setting<String> setting, String defaultValue) {
        String value = systemSettings.getString(setting.getKey());
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    Credentials credentials() {
//        return new Credentials("demo", "password", "2F822Rw39fx762MaV7Yy86jXGTC7sCDy");
//        return new Credentials("smartpay15", "QAZwsx123", "Cz6Yd4DJcp7nj79JqNCFA72bF4DgVQt8");
        /*Map<Settings.Setting, Object> map = systemSettings.get(Settings.PaymentPrimeRateSettings.SETTINGS);
        return new Credentials(map.getOrDefault(Settings.PaymentPrimeRateSettings.LOGIN, StringUtils.EMPTY).toString(),
                map.getOrDefault(Settings.PaymentPrimeRateSettings.PASSWORD, StringUtils.EMPTY).toString(),
                map.getOrDefault(Settings.PaymentPrimeRateSettings.API_KEY, StringUtils.EMPTY).toString());*/
        return new Credentials(
                property(Settings.PaymentPrimeRateSettings.LOGIN, "smartpay15"),
                property(Settings.PaymentPrimeRateSettings.PASSWORD, "QAZwsx123"),
                property(Settings.PaymentPrimeRateSettings.API_KEY, "Cz6Yd4DJcp7nj79JqNCFA72bF4DgVQt8"));
        // TODO 2020-05-02 : remove stub
    }

    public String urlForMethod(String url) {
        return gateway.urlForPaymentMethod(credentials(), url);
    }

    public PaymentMethod completeVault(String token) throws PaymentInstrumentProblem {
        VaultResponse vault = gateway.completeVault(credentials(), token);
        if(vault == null || !StringUtils.equals(EndResponse.RESULT_OK, vault.getResult())) {
            String message = vault == null ? null : vault.getResultText();
            logger.warn("error completing vault: {}", message);
            throw new PaymentInstrumentProblem(
                    StringUtils.defaultString(message, "Unknown error saving payment method"));
        }
        Billing billing = vault.getBilling();
        PaymentMethod paymentMethod;
        if (billing.creditCard()) {
            PaymentMethodCard card = new PaymentMethodCard();
            card.setNumber(billing.getCcNumber());
            card.setExpiration(billing.getCcExp());
            paymentMethod = card;
        } else {
            PaymentMethodAch ach = new PaymentMethodAch();
            ach.setAccount(billing.getAccountNumber());
            ach.setName(billing.getAccountName());
            ach.setRouting(billing.getRoutingNumber());
            paymentMethod = ach;
        }

//        paymentMethod.setBillingId(billing.getBillingId());
        paymentMethod.setVaultId(vault.getVaultId());
        return paymentMethod;
    }

    @Transactional
    public PaymentMethod save(String practiceId, PaymentMethod paymentMethod) {
        paymentMethod.setPractice(practiceDao.findOne(practiceId));
        return paymentMethodDao.save(paymentMethod);
    }

    long maxAttemptsAch() {
        return systemSettings.getLong(Settings.PaymentAttemptsSettings.NUMBER_OF_ATTEMPTS_ACH.getKey(), 2L);
    }

    long maxAttemptsCard() {
        return systemSettings.getLong(Settings.PaymentAttemptsSettings.NUMBER_OF_ATTEMPTS_CARD.getKey(), 4L);
    }

    List<PaymentAttempt> allAttempts(Payment payment) {
        return paymentAttemptDao.findByPaymentOrderByCreatedDesc(payment);
    }

    List<PaymentAttempt> currentRoundAttempts(Payment payment) {
        return paymentAttemptDao.findByPaymentAndRoundOrderByCreated(payment, payment.getCurrentRound());
    }

    /**
     * Prepares payment methods for an attempt.
     * <p/>
     * Uses <code>primary</code> method first, then preferred, then all other
     *
     * @param payment payment
     * @param primary (optional) primary method to use
     * @return ordered list of payment methods to use
     */
    List<PaymentMethod> prepareMethods(Payment payment, PaymentMethod primary) {
        List<PaymentMethod> allMethods =
                paymentMethodDao.findByPracticeAndLabelNotNullOrderByPreferredDesc(payment.getPractice());
        List<PaymentMethod> methods = new ArrayList<>(allMethods.size());
        if (primary != null) {
            methods.add(primary);
            allMethods.remove(primary);
        }

        methods.addAll(allMethods);
        return methods;
    }

    PaymentAttempt constructNewAttempt(Payment payment, PaymentMethod current) {
        logger.debug("using {} method [{}]: {}", current.type(), current.getId(), current.getLabel());
        PaymentAttempt attempt = new PaymentAttempt();
        attempt.setPayment(payment);
        attempt.setAmount(payment.getAmount());
        attemptMethod(attempt, current);
        attempt.setRound(payment.getCurrentRound());
        attempt.setStatus(PaymentAttempt.STATUS_NEW);
        return paymentAttemptDao.save(attempt);
    }

    private void attemptMethod(PaymentAttempt attempt, PaymentMethod method) {
        attempt.setMethod(method);
        attempt.setPaymentMethodLabel(method.getLabel());
    }

    String failedStatus(
            Collection<PaymentMethod> paymentMethods,
            long elapsedAttemptsAch, long elapsedAttemptsCard, long maxAttemptsAch, long maxAttemptsCard) {
        boolean canTryAgain =
                elapsedAttemptsAch < maxAttemptsAch
                        && paymentMethods.stream().anyMatch(PaymentMethodAch.class::isInstance)
                ||
                elapsedAttemptsCard < maxAttemptsCard
                        && paymentMethods.stream().anyMatch(PaymentMethodCard.class::isInstance);
        return canTryAgain ? Payment.STATUS_FAILED : Payment.STATUS_FAILED_FINAL;
    }

    boolean methodAllowed(PaymentMethod method,
                          long elapsedAttemptsAch, long elapsedAttemptsCard, long maxAttemptsAch, long maxAttemptsCard) {
        return method instanceof PaymentMethodAch && elapsedAttemptsAch < maxAttemptsAch
                || method instanceof PaymentMethodCard && elapsedAttemptsCard < maxAttemptsCard;
    }

    PaymentAttempt manualAttemptScheduled(Payment payment, List<PaymentAttempt> thisRoundAttempts) {
        return CollectionUtils.size(thisRoundAttempts) == 1
                && PaymentAttempt.STATUS_NEW.equals(thisRoundAttempts.get(0).getStatus())
                ? thisRoundAttempts.get(0) : null;
    }

    @Transactional
    public PaymentAttempt pickAttempt(Payment payment, PaymentMethod primary) {
        if (Payment.STATUS_PENDING.equals(payment.getStatus())) {
            List<PaymentAttempt> attempts = currentRoundAttempts(payment);
            return attempts.get(attempts.size() - 1); // last attempt should be pending
        } else {
            List<PaymentMethod> methods = prepareMethods(payment, primary);
            List<PaymentAttempt> allAttempts = allAttempts(payment);
            List<PaymentAttempt> thisRoundAttempts = currentRoundAttempts(payment);

            long attemptsNoAch = allAttempts.stream().filter(a -> a.getMethod() instanceof PaymentMethodAch).count();
            long attemptsNoCard = allAttempts.stream().filter(a -> a.getMethod() instanceof PaymentMethodCard).count();
            long maxAttemptsAch = maxAttemptsAch();
            long maxAttemptsCard = maxAttemptsCard();

            PaymentAttempt manual;
            PaymentMethod current = null;
            boolean found = false;

            if ((manual = manualAttemptScheduled(payment, thisRoundAttempts)) != null) { // a manual 'run now' payment
                current = manual.getMethod();
            }

            if (current == null) {
                for (PaymentMethod method : methods) {
                    current = method;
                    if (thisRoundAttempts.stream().noneMatch(a -> method.equals(a.getMethod()))) {
                        if (methodAllowed(method, attemptsNoAch, attemptsNoCard, maxAttemptsAch, maxAttemptsCard)) {
                            found = true;
                            break;
                        }
                    }
                }
            }

            if (manual != null) {
                if (found) {
                    attemptMethod(manual, current);
                }
                return manual;
            } else if (found) {
                return constructNewAttempt(payment, current);
            } else {
                int round = payment.getCurrentRound() + 1;
                payment.setCurrentRound(round);
                payment.setStatus(failedStatus(methods, attemptsNoAch, attemptsNoCard, maxAttemptsAch, maxAttemptsCard));
                payment.setLastAttemptsElapsed(LocalDateTime.now());
                paymentDao.save(payment);
                logger.debug("payment [{}] round set to {}, status: {}", payment.getId(), round, payment.getStatus());
                return null;
            }
        }
    }

    public PaymentAttempt lastUnfinished(Payment payment) {
        List<PaymentAttempt> attempts = paymentAttemptDao.findByPaymentOrderByCreatedDesc(payment);
        if (CollectionUtils.isEmpty(attempts)) {
            return null;
        } else {
            PaymentAttempt candidate = attempts.iterator().next();
            return PaymentAttempt.STATUS_NEW.equals(candidate.getStatus()) ? candidate : null;
        }
    }

    String comment(PaymentAttempt paymentAttempt) {
        String origin = "N/A";
        if (paymentAttempt.getPayment().getJobDay() != null) {
            origin = "day " + paymentAttempt.getPayment().getJobDay().getDate();
        } else if (paymentAttempt.getPayment().getJobInterview() != null) {
            origin = "interview " + paymentAttempt.getPayment().getJobInterview().getAcceptedOption().getDate();
        } else if (paymentAttempt.getPayment().getPermanentJobApplication() != null) {
            origin = paymentAttempt.getPayment().getPermanentJobApplication().getPermanentJobPosting().getName();
        }
        return "MDD: " + origin + " from " + paymentAttempt.getPayment().getPractice().getName();
    }

    void appendLog(PaymentAttempt attempt, String message) {
        attempt.setLog((attempt.getLog() == null ? "" : attempt.getLog() + "\n") + LocalDateTime.now() + " " + message);
    }

    void appendLog(Payment payment, String message) {
        PaymentAttempt attempt = paymentAttemptDao.findTopByPaymentOrderByCreatedDesc(payment);
        if (attempt == null) {
            // no attempt means a new (not mature) payment.
            // DONE is okay for both terminal operations and (amount) update,
            // as it does not affect other payment operations
            attempt = new PaymentAttempt();
            attempt.setPayment(payment);
            attempt.setStatus(PaymentAttempt.STATUS_DONE);
            attempt.setAmount(payment.getAmount());
            attempt.setRound(payment.getCurrentRound());
            attempt = paymentAttemptDao.save(attempt);
        }
        appendLog(attempt, message);
    }

    PaymentAttempt handleGatewayResult(PaymentAttempt paymentAttempt, Result result) {
        paymentAttempt.setStatus(result.getStatus());
        paymentAttempt.setGatewayStatus(result.getMessage());
        paymentAttempt.setGatewayId(result.getTransactionId());
        appendLog(paymentAttempt, result.getMessage());
        return paymentAttempt;
    }

    /**
     * Process attempt at gateway.
     * <p/>
     * If attempt not yet present at gateway, a payment is performed.
     *
     * @param paymentAttempt attempt
     * @return attempt with gateway payment status or <code>null</code> if no instrument
     */
    public PaymentAttempt processAttempt(PaymentAttempt paymentAttempt) {
        Result result = gateway.check(credentials(), paymentAttempt.getOrderId());
        if (result == null) {
            if (paymentAttempt.getMethod() == null) {
                // this is an (orphan?) manual attempt
                return null;
            }
            result = gateway.pay(credentials(), paymentAttempt.getMethod().getVaultId(),
                    paymentAttempt.getOrderId(), paymentAttempt.getAmount(), comment(paymentAttempt));
        }
        return handleGatewayResult(paymentAttempt, result);
    }

    /**
     * Check attempts with gateway.
     *
     * @param paymentAttempt payment attempt
     * @return attempt with gateway status set or <code>null</code> if not found at gateway
     */
    public PaymentAttempt checkAttempt(PaymentAttempt paymentAttempt) {
        Result result = gateway.check(credentials(), paymentAttempt.getOrderId());
        if (result == null) {
            // never paid
            return null;
        }
        return handleGatewayResult(paymentAttempt, result);
    }

    public Payment handleResults(PaymentAttempt paymentAttempt) {
        paymentAttempt = self.handlePaymentAttemptResults(paymentAttempt);
        publishPaymentEvent(paymentAttempt);
        return paymentAttempt.getPayment();
    }

    @Transactional
    public PaymentAttempt handlePaymentAttemptResults(PaymentAttempt paymentAttempt) {
        paymentAttempt = paymentAttemptDao.save(paymentAttempt);
        Payment payment = paymentAttempt.getPayment();

        switch (paymentAttempt.getStatus()) {
            case PaymentAttempt.STATUS_PAID:
                payment.setStatus(Payment.STATUS_PAID);
                payment.setLastAttemptsElapsed(LocalDateTime.now());
                break;
            case PaymentAttempt.STATUS_PENDING:
                payment.setStatus(Payment.STATUS_PENDING);
                break;
            case PaymentAttempt.STATUS_FAILED:
                Settings.Setting penalty = paymentAttempt.getMethod() instanceof PaymentMethodAch
                        ? Settings.PaymentAttemptsSettings.ACH_PENALTY
                        : Settings.PaymentAttemptsSettings.CC_PENALTY;
                payment.setAmount(payment.getAmount().add(new BigDecimal(systemSettings.getLong(penalty.getKey(), 0L))));
                payment.setStatus(Payment.STATUS_FAILED);
                break;
        }
        payment.setMethod(paymentAttempt.getMethod() == null ? PaymentMethod.TYPE_CC : paymentAttempt.getMethod().type());

        payment = paymentDao.save(paymentAttempt.getPayment());
        paymentAttempt.setPayment(payment);

        return paymentAttempt;
    }

    private void publishPaymentEvent(PaymentAttempt paymentAttempt) {
        if (PAYMENT_STATUSES_FOR_NOTIFICATION.contains(paymentAttempt.getStatus())) {
            paymentStatusEventBus.publishEvent(PaymentStatusEvent.create(paymentAttempt, paymentAttempt.getPayment()));
        }
    }

    @Transactional
    public void deleteAttempt(PaymentAttempt attempt) {
        paymentAttemptDao.delete(attempt);
    }

    @Transactional
    public void deleteMethod(PaymentMethod method) {
        paymentAttemptDao.deleteMethodReferences(method.getId());
        paymentMethodDao.delete(method);
    }

    public String urlForPayment(PaymentAttempt attempt, String url) {
        return gateway.urlForPayment(credentials(), attempt.getOrderId(), attempt.getAmount(), comment(attempt), url);
    }

    public Result completeTransaction(String token) {
        return gateway.completeTransaction(credentials(), token);
    }

    @Transactional
    public Payment completeAttempt(String id, Result result) {
        return handleResults(handleGatewayResult(paymentAttemptDao.findByOrderId(id), result));
    }

    String appendOptionComment(String comment) {
        return StringUtils.isBlank(comment) ? "" : ". " + comment;
    }

    @Transactional
    public void commitPayment(Payment payment, String status, String comment, BigDecimal amount) {
        payment.setStatus(status);
        payment.setMethod(PaymentMethod.TYPE_MANUAL);
        paymentAmount(payment, amount);
        appendLog(payment, "manually set to " + status + appendOptionComment(comment));
        paymentDao.save(payment);
    }

    void paymentAmount(Payment payment, BigDecimal amount) {
        if (amount != null && amount.signum() > 0) {
            payment.setAmount(amount);
        }
    }

    PaymentAttempt newPayment(Payment payment, PaymentAttempt existing, BigDecimal amount, String methodId, String comment) {
        paymentAmount(payment, amount);
        PaymentAttempt attempt = existing == null ? new PaymentAttempt() : existing;
        attempt.setPayment(payment);
        attempt.setAmount(payment.getAmount());
        if (StringUtils.isBlank(methodId)) {
            appendLog(attempt, "instant payment" + appendOptionComment(comment));
        } else {
            payment.setCurrentRound(payment.getCurrentRound() + 1);
            attemptMethod(attempt, paymentMethodDao.findOne(methodId));
            appendLog(attempt, "run manually with " + attempt.getPaymentMethodLabel() + appendOptionComment(comment));
        }
        attempt.setRound(payment.getCurrentRound());
        paymentDao.save(payment);
        return paymentAttemptDao.save(attempt);
    }

    @Transactional
    public PaymentAttempt runPayment(Payment payment, BigDecimal amount, String methodId, String comment) {
        return newPayment(payment, manualAttemptScheduled(payment, currentRoundAttempts(payment)), amount, methodId, comment);
    }

    @Transactional
    public PaymentAttempt externalPayment(Payment payment, BigDecimal amount, String comment) {
        return newPayment(payment, null, amount, null, comment);
    }

    @Transactional
    public void updatePayment(Payment payment, BigDecimal amount, String comment) {
        appendLog(payment, "change amount from " + payment.getAmount() + " to " + amount + appendOptionComment(comment));
        payment.setAmount(amount);
        paymentDao.save(payment);
    }

    public void deleteVault(String id) {
        gateway.deleteVault(credentials(), id);
    }
}
