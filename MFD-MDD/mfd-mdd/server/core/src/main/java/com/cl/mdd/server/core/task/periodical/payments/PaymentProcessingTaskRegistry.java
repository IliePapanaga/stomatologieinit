package com.cl.mdd.server.core.task.periodical.payments;

import com.cl.mdd.server.core.service.payment.PaymentService;
import com.cl.mdd.server.core.task.TaskRegistry;
import com.cl.mdd.server.jobs.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry for payments-related jobs.
 */
@TaskRegistry
public class PaymentProcessingTaskRegistry {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PaymentService paymentService;

    public PaymentProcessingTaskRegistry(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Job(cron = "${payment.job.payments.cron:0 1/5 * * * ? *}", name = "PROCESS CURRENT PAYMENTS")
    public void processPayments() {
        logger.debug("running payments processing job");
        paymentService.processCurrentPayments();
    }

    @Job(cron = "${payment.job.release.cron:0 2/5 * * * ? *}", name = "RELEASE PAYMENT LOCKS")
    public void releasePayments() {
        logger.debug("running release payments job");
        paymentService.releasePayments();
    }

    @Job(cron = "${payment.job.cleanup.methods.cron:0 52 * * * ? *}", name = "CLEANUP INCOMPLETE PAYMENT METHODS")
    public void cleanupMethods() {
        logger.debug("running cleanup payment methods job");
        paymentService.cleanupPaymentMethods();
    }

    @Job(cron = "${payment.job.expiring.cards.cron:0 30 8 2 * ? *}", name = "NOTIFY EXPIRING CREDIT CARDS")
    public void notifyExpiringCards() {
        logger.debug("running expiring credit card notifications job");
        paymentService.notifyExpiringCards();
    }
}
