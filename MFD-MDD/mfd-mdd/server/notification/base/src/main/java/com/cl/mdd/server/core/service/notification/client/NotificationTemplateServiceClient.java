package com.cl.mdd.server.core.service.notification.client;

import com.cl.mdd.server.core.data.model.notification.FindNotificationTemplatesQuery;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModel;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModelList;

import java.util.List;

/**
 * Client for notification service that supports management of notification templates
 */
public interface NotificationTemplateServiceClient {

    NotificationTemplateModel save(NotificationTemplateModel template);

    NotificationTemplateModel get(String id);

    NotificationTemplateModelList list(Integer page, Integer perPage, List<FindNotificationTemplatesQuery.NotificationTemplatesOrder> orders);

    void delete(String id);
}
