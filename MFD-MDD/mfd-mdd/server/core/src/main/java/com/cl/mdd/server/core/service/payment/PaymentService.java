package com.cl.mdd.server.core.service.payment;

import com.cl.mdd.server.core.data.model.payment.*;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.service.Service;

import javax.validation.Valid;

/**
 * Payment operations interface.
 */
public interface PaymentService extends Service {

    /**
     * First step to start adding a payment method to vault.
     * <p />
     * Performed by practice owner.
     * @param practiceId practice ID
     * @return form URL
     */
    String prepareVault(String practiceId);

    /**
     * Using redirect-based PCI DSS safe payments approach,
     * this completes the save payment method operation
     * (started by {@link #prepareVault(String)}
     * as reported by the gateway.
     * <p />
     * Performed by gateway within practice owner's session.
     * @param localId local identifier, e.g. the client identifier
     * @param token payment gateway token
     * @return local payment method ID
     * @throws PaymentInstrumentProblem on save error
     */
    String completeVault(String localId, String token) throws PaymentInstrumentProblem;

    /**
     * Get a payment method.
     * @param id ID
     * @return {@link CreditCard} or {@link BankAccount} or <code>null</code> if not found
     */
    PaymentInstrument paymentMethod(String id);

    /**
     * Save a payment method.
     * @param paymentInstrument payment method
     * @return payment method
     */
    PaymentInstrument save(@Valid PaymentInstrument paymentInstrument);

    /**
     * Delete payment method.
     * @param paymentInstrumentId payment method ID
     */
    void delete(String paymentInstrumentId);

    /**
     * Query payment methods.
     * @param query query filters
     * @return query result
     */
    QueryResult<PaymentInstrument> fetchPracticePaymentMethods(PaymentMethodQuery query);

    /**
     * Using redirect-based PCI DSS safe payments approach,
     * this completes the payment operation as reported by the gateway.
     * <p />
     * The operation is started by system administrator and completed
     * by gateway within the administrator's session.
     * @param localId local identifier, e.g. the transaction identifier
     * @param token payment gateway token
     * @return whether the payment is okay
     */
    boolean completeTransaction(String localId, String token);

    /**
     * Periodically process payments that require action.
     */
    void processCurrentPayments();

    /**
     * Periodically release payments locked by (failed) operations
     * that did not correctly unlock them.
     */
    void releasePayments();

    /**
     * Periodically delete payment methods
     * that did not finish to save completely.
     */
    void cleanupPaymentMethods();

    /**
     * Periodically notify owners of
     * credit cards that are about to expire.
     */
    void notifyExpiringCards();

    /**
     * Request a manual operation.
     * @param request manual operation request
     * @return response
     */
    PaymentOptionsResponse requestManualOperation(@Valid PaymentOptionsModel request);

    /**
     * Get a payment by ID.
     * @param id payment ID
     * @return payment or <code>null</code>
     */
    PaymentModel get(String id);

    /**
     * Get payment details by ID.
     * @param id payment ID
     * @return payment details or <code>null</code>
     */
    PaymentDetails details(String id);

    /**
     * Query payment for a Practice.
     * @param query query
     * @return query result
     */
    QueryResult<PaymentModel> fetchPracticePayments(PaymentQuery query);

    /**
     * Query all payments.
     * @param query query
     * @return query result
     */
    QueryResult<PaymentModel> fetch(PaymentQuery query);
}
