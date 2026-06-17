package com.cl.mdd.server.core.event.impl.handler;

import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.SignUpCompletedEvent;
import com.cl.mdd.server.core.service.notification.Notification;
import com.cl.mdd.server.core.service.notification.NotificationService;
import com.cl.mdd.server.core.service.notification.UserVariables;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.ImmutableMap;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.copyOf;

@Consumer
public class SignUpCompletedEventHandler implements EventHandler<SignUpCompletedEvent> {

    private static final String PROFESSIONAL_SIGN_UP_COMPLETED = "PROFESSIONAL_SIGN_UP_COMPLETED";

    private static final String PRACTICE_OWNER_SIGN_UP_COMPLETED = "PRACTICE_OWNER_SIGN_UP_COMPLETED";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SystemUserDao systemUserDao;

    @Autowired
    private UserVariables userVariables;

    @Override
    @NotificationDefinition(value = PRACTICE_OWNER_SIGN_UP_COMPLETED,
            predefined = {UserVariables.class})
    @NotificationDefinition(value = PROFESSIONAL_SIGN_UP_COMPLETED,
            predefined = {UserVariables.class}
    )
    public void onEvent(SignUpCompletedEvent event, long sequence, boolean endOfBatch) {
        User newlySignedUp = userDao.findOne(event.getUserId());

        Map<String, String> context = new HashMap<>();
        userVariables.supply(newlySignedUp, context);

        ImmutableMap<String, String> immutableContext = copyOf(context);

        final String notificationType = newlySignedUp instanceof Professional ? PROFESSIONAL_SIGN_UP_COMPLETED : PRACTICE_OWNER_SIGN_UP_COMPLETED;
        systemUserDao.findAll().stream().filter(user -> !user.getStatus().equals(User.INACTIVE)).forEach(systemUser -> {
            Notification notification = new Notification();
            notification.setEmail(systemUser.getUsername());
            notification.setPhone(systemUser.getContact().getPhone());
            notification.setType(notificationType);
            notification.setContext(immutableContext);
            notificationService.send(notification);
        });
    }

}
