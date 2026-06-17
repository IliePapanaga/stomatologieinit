package com.cl.mdd.server.core.event.impl.handler;

import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.UserStatusChangedEvent;
import com.cl.mdd.server.core.service.notification.AdminVariables;
import com.cl.mdd.server.core.service.notification.Notification;
import com.cl.mdd.server.core.service.notification.NotificationService;
import com.cl.mdd.server.core.service.notification.UserVariables;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.copyOf;

@Consumer
public class UserStatusChangedEventHandler implements EventHandler<UserStatusChangedEvent> {

    private static final String USER_ACTIVATED = "USER_ACTIVATED";

    private static final String USER_DEACTIVATED = "USER_DEACTIVATED";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserVariables userVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @NotificationDefinition(value = USER_ACTIVATED,
            predefined = {
                    UserVariables.class,
                    AdminVariables.class
            }
    )
    @NotificationDefinition(value = USER_DEACTIVATED,
            predefined = {
                    UserVariables.class,
                    AdminVariables.class
            }
    )
    public void onEvent(UserStatusChangedEvent event, long sequence, boolean endOfBatch) {
        User user = userDao.findOne(event.getUserId());

        final String notificationType = User.ACTIVE.equals(event.getStatus())
                ? USER_ACTIVATED
                : USER_DEACTIVATED;

        Notification notification = new Notification();
        notification.setEmail(user.getUsername());
        notification.setPhone(user.getContact().getPhone());
        notification.setType(notificationType);

        Map<String, String> context = Maps.newHashMap();

        userVariables.supply(user, context);
        adminVariables.supply(null, context);

        notification.setContext(copyOf(context));
        notificationService.send(notification);
    }
}
