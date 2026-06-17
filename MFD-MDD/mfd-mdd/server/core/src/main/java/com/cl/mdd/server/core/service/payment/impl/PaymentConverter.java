package com.cl.mdd.server.core.service.payment.impl;

import com.cl.mdd.server.core.data.model.payment.*;
import com.cl.mdd.server.core.data.persistent.model.payment.*;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class PaymentConverter {

    public PaymentModel toPaymentModel(Payment payment) {
        if (payment == null) {
            return null;
        }
        PaymentModel paymentModel = new PaymentModel()
                .withId(payment.getId())
                .withDate(payment.getCreated().toLocalDate())
                .withPracticeId(payment.getPractice().getId())
                .withPracticeFirstName(payment.getPractice().getOwner().getContact().getName().getFirst())
                .withPracticeLastName(payment.getPractice().getOwner().getContact().getName().getLast())
                .withOffice(payment.getLocation().getName())
                .withLocation(payment.getLocation().getContact().getAddress().getCity())
                .withAmount(payment.getAmount())
                .withStatus(payment.getStatus())
                .withMethod(payment.getMethod());
        JobPosting posting = null;
        if (payment.getJobDay() != null) {
            paymentModel.setJobDayId(payment.getJobDay().getId());
            posting = payment.getJobDay().getJobPosting();
        } else if (payment.getJobInterview() != null) {
            paymentModel.setJobInterviewId(payment.getJobInterview().getId());
            posting = payment.getJobInterview().getApplication().getPermanentJobPosting();
        } else if (payment.getPermanentJobApplication() != null) {
            paymentModel.setJobApplicationId(payment.getPermanentJobApplication().getId());
            posting = payment.getPermanentJobApplication().getPermanentJobPosting();
        }

        if (posting != null) {
            paymentModel.setLabel(posting.getName());
        }

        Professional pro = payment.getProfessional();
        if (pro != null) {
            paymentModel
                    .withProFirstName(pro.getContact().getName().getFirst())
                    .withProLastName(pro.getContact().getName().getLast());
        }

        return paymentModel;
    }

    public PaymentInstrument toMethodModel(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return null;
        }
        if (paymentMethod instanceof PaymentMethodCard) {
            return (PaymentInstrument) new CreditCard()
                    .withNumber(((PaymentMethodCard) paymentMethod).getNumber())
                    .withExpiration(((PaymentMethodCard) paymentMethod).getExpiration())
                    .withId(paymentMethod.getId())
                    .withLabel(paymentMethod.getLabel())
                    .withPreferred(paymentMethod.isPreferred());
        } else if (paymentMethod instanceof PaymentMethodAch) {
            return (PaymentInstrument) new BankAccount()
                    .withName(((PaymentMethodAch) paymentMethod).getName())
                    .withAccount(((PaymentMethodAch) paymentMethod).getAccount())
                    .withRouting(((PaymentMethodAch) paymentMethod).getRouting())
                    .withId(paymentMethod.getId())
                    .withLabel(paymentMethod.getLabel())
                    .withPreferred(paymentMethod.isPreferred());
        } else {
            throw new IllegalStateException("Unknown Payment Method type: " + paymentMethod.getClass());
        }
    }

    public PaymentDetails toPaymentDetails(Payment payment) {
        if(payment == null) {
            return null;
        }
        PaymentDetails details = new PaymentDetails();
        details.setPaymentId(payment.getId());
        details.setStatus(payment.getStatus());
        details.setSteps(CollectionUtils.emptyIfNull(payment.getAttempts()).stream()
                .sorted(Comparator.comparing(PaymentAttempt::getCreated))
                .map(a -> new PaymentStep(
                        a.getCreated(), a.getStatus(), a.getAmount(), a.getPaymentMethodLabel(),
                        a.getLog(), a.getGatewayId(), a.getGatewayStatus()))
                .collect(Collectors.toList()));
        return details;
    }

}
