package com.cl.mdd.server.core.service.notification.client.mock;

import com.cl.mdd.server.core.data.model.notification.FindNotificationTemplatesQuery;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModel;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModelList;
import com.cl.mdd.server.core.service.notification.client.NotificationTemplateServiceClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Mock implementation of {@link NotificationTemplateServiceClient}
 */
public class MockNotificationTemplateServiceClient implements NotificationTemplateServiceClient {

    @Override
    public NotificationTemplateModel save(NotificationTemplateModel template) {
        throw new UnsupportedOperationException("Notification template management is not supported with mock notification service");
    }

    @Override
    public NotificationTemplateModel get(String id) {
        throw new UnsupportedOperationException("Notification template management is not supported with mock notification service");
    }

    @Override
    public NotificationTemplateModelList list(Integer page, Integer perPage, List<FindNotificationTemplatesQuery.NotificationTemplatesOrder> orders) {
        throw new UnsupportedOperationException("Notification template management is not supported with mock notification service");
    }

    @Override
    public void delete(String id) {
        throw new UnsupportedOperationException("Notification template management is not supported with mock notification service");
    }
}
