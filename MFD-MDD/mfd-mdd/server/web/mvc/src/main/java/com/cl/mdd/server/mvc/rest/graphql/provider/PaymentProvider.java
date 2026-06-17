package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.payment.*;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.manager.payment.PaymentManager;
import com.cl.mdd.server.core.security.SecurityAccess;
import com.cl.mdd.server.core.service.payment.PaymentService;
import com.cl.mdd.server.mvc.rest.graphql.model.Connection;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

import static com.cl.mdd.server.core.data.model.payment.PaymentOptionsModel.*;
import static com.cl.mdd.server.mvc.rest.graphql.model.Connection.fromQueryResult;

/**
 * Expose payments to Graph QL.
 */
@Component
public class PaymentProvider implements GraphQLProvider {

    private final PaymentService paymentService;
    private final PaymentManager paymentManager;
    private final SecurityAccess securityAccess;

    public PaymentProvider(PaymentService paymentService, PaymentManager paymentManager, SecurityAccess securityAccess) {
        this.paymentService = paymentService;
        this.paymentManager = paymentManager;
        this.securityAccess = securityAccess;
    }

    @GraphQLQuery(name = "paymentMethod")
    public PaymentInstrument paymentMethod(@GraphQLArgument(name = "id") String id) {
        return paymentService.paymentMethod(id);
    }

    @GraphQLQuery(name = "paymentForm")
    public PaymentForm paymentForm() {
        return new PaymentForm(paymentService.prepareVault(securityAccess.currentUserId()));
    }

    private PaymentInstrument savePaymentMethod(PaymentInstrument method) {
        return paymentService.save(method);
    }
    @GraphQLMutation(name = "saveCreditCard")
    public PaymentInstrument saveCreditCard(@GraphQLArgument(name = "card") CreditCard method) {
        return savePaymentMethod(method);
    }
    @GraphQLMutation(name = "saveBankAccount")
    public PaymentInstrument saveBankAccount(@GraphQLArgument(name = "ach") BankAccount method) {
        return savePaymentMethod(method);
    }

    @GraphQLMutation(name = "deletePaymentMethod")
    public void deletePaymentMethod(@GraphQLArgument(name = "id") String id) {
        paymentService.delete(id);
    }

    @GraphQLQuery(name = "paymentMethods")
    public Connection<PaymentInstrument> paymentMethods(
            @GraphQLArgument(name = "page") Integer page, @GraphQLArgument(name = "perPage") Integer perPage,
            @GraphQLArgument(name = "orders") List<PaymentMethodQuery.PaymentMethodOrder> orders) {
        PaymentMethodQuery query = new PaymentMethodQuery();
        query.getFilters().withPracticeId(securityAccess.currentUserId());
        query.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);
        return fromQueryResult(paymentService.fetchPracticePaymentMethods(query));
    }

    @GraphQLQuery(name = "payment")
    public PaymentModel payment(@GraphQLArgument(name = "id") String id) {
        return paymentService.get(id);
    }

    @GraphQLQuery(name = "paymentDetails")
    public PaymentDetails paymentDetails(@GraphQLArgument(name = "id") String id) {
        return paymentService.details(id);
    }

    Connection<PaymentModel> queryPayments(
            String practiceId, String status, String method, ZonedDateTime from, ZonedDateTime to,
            Double distance, Double latitude, Double longitude,
            List<PaymentQuery.PaymentOrder> orders, Integer page, Integer perPage,
            Function<PaymentQuery, QueryResult<PaymentModel>> loader) {
        PaymentQuery query = new PaymentQuery();
        query.getFilters()
                .withPracticeId(practiceId)
                .withStatus(status)
                .withMethod(method)
                .withFrom(from)
                .withTo(to)
                .withDistance(distance)
                .withLatitude(latitude)
                .withLongitude(longitude);
        query.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return fromQueryResult(loader.apply(query));
    }

    @GraphQLQuery(name = "practicePayments")
    public Connection<PaymentModel> practicePayments(
            @GraphQLArgument(name = "page") Integer page, @GraphQLArgument(name = "perPage") Integer perPage,
            @GraphQLArgument(name = "status") String status, @GraphQLArgument(name = "method") String method,
            @GraphQLArgument(name = "dateFrom") ZonedDateTime from, @GraphQLArgument(name = "dateTo") ZonedDateTime to,
            @GraphQLArgument(name = "orders") List<PaymentQuery.PaymentOrder> orders) {
        return queryPayments(securityAccess.currentUserId(), status, method, from, to, null, null, null,
                orders, page, perPage, paymentService::fetchPracticePayments);
    }

    @GraphQLQuery(name = "payments")
    public Connection<PaymentModel> allPayments(
            @GraphQLArgument(name = "page") Integer page, @GraphQLArgument(name = "perPage") Integer perPage,
            @GraphQLArgument(name = "practiceId") String practiceId,
            @GraphQLArgument(name = "status") String status, @GraphQLArgument(name = "method") String method,
            @GraphQLArgument(name = "dateFrom") ZonedDateTime from, @GraphQLArgument(name = "dateTo") ZonedDateTime to,
            @GraphQLArgument(name = "distance") Double distance,
            @GraphQLArgument(name = "lat") Double lat, @GraphQLArgument(name = "lng") Double lng,
            @GraphQLArgument(name = "orders") List<PaymentQuery.PaymentOrder> orders) {
        return queryPayments(practiceId, status, method, from, to, distance, lat, lng, orders, page, perPage,
                paymentService::fetch);
    }

    @GraphQLMutation(name = "paymentManualOperation")
    public PaymentOptionsResponse manualOperation(
            @GraphQLArgument(name = "paymentId") String paymentId,
            @GraphQLArgument(name = "operation",
                    description = "Can be one of " + MODIFY + ", " + COMPLETE + ", "
                            + RUN + ", " + CANCEL + ", " + INSTANT_PAY + ", " + PARTIAL) String option,
            @GraphQLArgument(name = "amount") BigDecimal amount,
            @GraphQLArgument(name = "comment") String comment,
            @GraphQLArgument(name = "paymentMethodId") String paymentMethodId) {
        return paymentService.requestManualOperation(new PaymentOptionsModel()
                .withPaymentId(paymentId)
                .withOption(option)
                .withAmount(amount)
                .withComment(comment)
                .withPaymentMethodId(paymentMethodId));
    }

    @GraphQLQuery(name = "practicePaymentMethods")
    public Connection<PaymentInstrument> practicePaymentMethods(
            @GraphQLArgument(name = "page") Integer page, @GraphQLArgument(name = "perPage") Integer perPage,
            @GraphQLArgument(name = "orders") List<PaymentMethodQuery.PaymentMethodOrder> orders,
            @GraphQLArgument(name = "practiceId") String practiceId) {
        PaymentMethodQuery query = new PaymentMethodQuery();
        query.getFilters().withPracticeId(practiceId);
        query.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);
        return fromQueryResult(paymentService.fetchPracticePaymentMethods(query));
    }

    @GraphQLQuery(name = "permanentPaymentDetailsPreview")
    public PaymentPermanentInfo permanentPaymentDetailsPreview(@GraphQLArgument(name = "applicationId") String applicationId) {
        return paymentManager.getPermanentPaymentInfo(applicationId);
    }
}
