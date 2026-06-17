package com.cl.mdd.server.core.service.payment.impl;

import com.cl.mdd.server.core.data.model.payment.BankAccount;
import com.cl.mdd.server.core.data.model.payment.CreditCard;
import com.cl.mdd.server.core.data.model.payment.PaymentDetails;
import com.cl.mdd.server.core.data.model.payment.PaymentModel;
import com.cl.mdd.server.core.data.persistent.model.common.embeddable.FullName;
import com.cl.mdd.server.core.data.persistent.model.contact.Address;
import com.cl.mdd.server.core.data.persistent.model.contact.Contact;
import com.cl.mdd.server.core.data.persistent.model.payment.*;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;

public class PaymentConverterTest {

    private PaymentConverter converter = new PaymentConverter();

    @Test
    public void testToPaymentModelNull() {
        Assert.assertNull(converter.toPaymentModel(null));
    }

    @Test
    public void testToPaymentModel() {
        Payment payment = new Payment();
        payment.setCreated(ZonedDateTime.now());
        payment.setPractice(new Practice());
        payment.getPractice().setId("52");
        payment.getPractice().setOwner(new PracticeOwner());
        payment.getPractice().getOwner().setContact(new Contact());
        payment.getPractice().getOwner().getContact().setName(new FullName());
        payment.getPractice().getOwner().getContact().getName().setLast("last");
        payment.setJobDay(new JobDay());
        payment.getJobDay().setJobPosting(new TemporaryJobPosting());
        payment.getJobDay().getJobPosting().setLocation(new PracticeLocation());
        payment.getJobDay().getJobPosting().getLocation().setName("loc");
        payment.getJobDay().getJobPosting().getLocation().setContact(new Contact());
        payment.getJobDay().getJobPosting().getLocation().getContact().setAddress(new Address());
        payment.getJobDay().getJobPosting().getLocation().getContact().getAddress().setCity("city");
        payment.getJobDay().getJobPosting().setName("posting");
        payment.setLocation(payment.getJobDay().getJobPosting().getLocation());
        payment.setAmount(BigDecimal.TEN);
        payment.setStatus("test");
        PaymentModel model = converter.toPaymentModel(payment);
        Assert.assertEquals(payment.getId(), model.getId());
        Assert.assertEquals(payment.getStatus(), model.getStatus());
        Assert.assertEquals(payment.getJobDay().getJobPosting().getName(), model.getLabel());
        Assert.assertEquals(payment.getAmount(), model.getAmount());
        Assert.assertEquals(payment.getPractice().getOwner().getContact().getName().getLast(), model.getPracticeLastName());
    }

    @Test
    public void testToMethodModelNull() {
        Assert.assertNull(converter.toMethodModel(null));
    }

    @Test(expected = IllegalStateException.class)
    public void testToMethodModelUnknown() {
        converter.toMethodModel(new PaymentMethod() {
            @Override
            public String type() {
                return "test";
            }
        });
    }

    @Test
    public void testToMethodModelCard() {
        PaymentMethodCard method = new PaymentMethodCard();
        method.setNumber("123");
        method.setId("52");
        CreditCard instrument = (CreditCard) converter.toMethodModel(method);
        Assert.assertEquals(method.getNumber(), instrument.getNumber());
        Assert.assertEquals(method.getId(), instrument.getId());
    }

    @Test
    public void testToMethodModelAch() {
        PaymentMethodAch method = new PaymentMethodAch();
        method.setAccount("123");
        method.setId("52");
        BankAccount instrument = (BankAccount) converter.toMethodModel(method);
        Assert.assertEquals(method.getAccount(), instrument.getAccount());
        Assert.assertEquals(method.getId(), instrument.getId());
    }

    @Test
    public void testToPaymentDetailsNull() {
        Assert.assertNull(converter.toPaymentDetails(null));
    }

    @Test
    public void testToPaymentDetails() {
        Payment p = new Payment();
        p.setId("52");
        PaymentAttempt attempt = new PaymentAttempt();
        attempt.setCreated(LocalDateTime.now());
        attempt.setGatewayId("123");
        p.setAttempts(Collections.singleton(attempt));
        PaymentDetails details = converter.toPaymentDetails(p);
        Assert.assertEquals(p.getId(), details.getPaymentId());
        Assert.assertEquals(1, details.getSteps().size());
        Assert.assertEquals(attempt.getCreated(), details.getSteps().get(0).getDate());
        Assert.assertEquals(attempt.getGatewayId(), details.getSteps().get(0).getGatewayId());
    }

}
