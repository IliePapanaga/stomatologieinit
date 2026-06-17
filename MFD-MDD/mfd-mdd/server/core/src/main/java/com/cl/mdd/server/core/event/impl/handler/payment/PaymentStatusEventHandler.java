package com.cl.mdd.server.core.event.impl.handler.payment;

import com.cl.mdd.server.core.data.persistent.access.payment.PaymentAttemptDao;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentAttempt;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.SystemUser;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.event.type.payment.PaymentStatusEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class PaymentStatusEventHandler {

    static final String PAYMENT_SUCCESSFUL_NOTIFICATION = "PAYMENT_SUCCESSFUL_NOTIFICATION";
    static final String PAYMENT_FAILED_NOTIFICATION = "PAYMENT_FAILED_NOTIFICATION";
    static final String PAYMENT_RESULT_NOTIFICATION_FOR_SYSTEM = "PAYMENT_RESULT_NOTIFICATION_FOR_SYSTEM";

    private final PaymentAttemptDao attemptDao;

    private final SystemUserDao systemUserDao;

    private final NotificationService notificationService;

    private final PracticeOwnerVariables practiceOwnerVariables;

    private final PaymentAttemptVariables paymentAttemptVariables;

    private final AdminVariables adminVariables;

    @Autowired
    public PaymentStatusEventHandler(PaymentAttemptDao attemptDao, SystemUserDao systemUserDao,
                                     NotificationService notificationService, PracticeOwnerVariables practiceOwnerVariables,
                                     PaymentAttemptVariables paymentAttemptVariables, AdminVariables adminVariables) {
        this.attemptDao = attemptDao;
        this.systemUserDao = systemUserDao;
        this.practiceOwnerVariables = practiceOwnerVariables;
        this.notificationService = notificationService;
        this.adminVariables = adminVariables;
        this.paymentAttemptVariables = paymentAttemptVariables;
    }

    /**
     * Notifies practice owner about the event if it matches the desired type.
     *
     * @param event           payment event
     * @param conditionStatus status to check
     */
    @Transactional(readOnly = true)
    public void notifyPractice(PaymentStatusEvent event, String conditionStatus) {
        if (StringUtils.equals(conditionStatus, event.getStatus())) {
            PaymentAttempt paymentAttempt = attemptDao.findOne(event.getAttemptId());
            PracticeOwner practiceOwner = paymentAttempt.getPayment().getPractice().getOwner();

            Map<String, String> context = Maps.newHashMap();

            practiceOwnerVariables.supply(practiceOwner, context);
            paymentAttemptVariables.supply(paymentAttempt, context);
            adminVariables.supply(null, context);

            String notificationType = PaymentAttempt.STATUS_PAID.equals(paymentAttempt.getStatus())
                    ? PAYMENT_SUCCESSFUL_NOTIFICATION
                    : PAYMENT_FAILED_NOTIFICATION;

            Notification notification = notification(notificationType, practiceOwner, context);
            notificationService.send(notification);
        }
    }

    @Transactional(readOnly = true)
    public void notifySystem(PaymentStatusEvent event) {
        PaymentAttempt paymentAttempt = attemptDao.findOne(event.getAttemptId());
        Map<String, String> context = Maps.newHashMap();

        paymentAttemptVariables.supply(paymentAttempt, context);

        systemUserDao.findAll().stream()
                .filter(this::isActiveUser)
                .forEach(admin -> notificationService.send(notification(PAYMENT_RESULT_NOTIFICATION_FOR_SYSTEM, admin, context)));
    }

    private boolean isActiveUser(SystemUser user) {
        return !user.getStatus().equals(User.INACTIVE);
    }

    private Notification notification(String type, User user, Map<String, String> context) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setEmail(user.getContact().getEmail());
        notification.setPhone(user.getContact().getPhone());
        notification.setContext(context);
        return notification;
    }
}
