package com.cl.mdd.server.core.service.notification;

// TODO: it is used from the service implementations, maybe this is a Manager rather than service?
public interface NotificationService {

    void send(Notification notification);
}
