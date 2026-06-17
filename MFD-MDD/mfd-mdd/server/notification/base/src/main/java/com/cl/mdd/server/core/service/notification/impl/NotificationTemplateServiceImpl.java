package com.cl.mdd.server.core.service.notification.impl;

import com.cl.mdd.server.core.data.model.notification.FindNotificationTemplatesQuery;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModel;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModelList;
import com.cl.mdd.server.core.data.model.notification.NotificationTypeDescriptorModel;
import com.cl.mdd.server.core.service.notification.NotificationTemplateService;
import com.cl.mdd.server.core.service.notification.client.NotificationTemplateServiceClient;
import com.cl.mdd.server.core.service.notification.definition.impl.NotificationTypeDescriptorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Simple implementation of {@link NotificationTemplateService} that delegates all operations to {@link NotificationTemplateServiceClient}
 */
@Service("MDD-NotificationTemplateService")
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private final NotificationTemplateServiceClient notificationTemplateServiceClient;

    private final NotificationTypeDescriptorRegistry notificationTypeDescriptorRegistry;

    @Autowired
    public NotificationTemplateServiceImpl(final NotificationTemplateServiceClient notificationTemplateServiceClient,
                                           final NotificationTypeDescriptorRegistry notificationTypeDescriptorRegistry) {
        this.notificationTemplateServiceClient = notificationTemplateServiceClient;
        this.notificationTypeDescriptorRegistry = notificationTypeDescriptorRegistry;
    }

    @Override
    public NotificationTemplateModel save(NotificationTemplateModel template) {
        fillAdditionalFields(template);
        return notificationTemplateServiceClient.save(template);
    }

    protected void fillAdditionalFields(NotificationTemplateModel template) {
        Optional<NotificationTypeDescriptorModel> descriptor = notificationTypeDescriptorRegistry.byType(template.getType());

        if (descriptor.isPresent()) {
            template.setName(descriptor.get().getName());
            template.setDescription(descriptor.get().getDescription());
        }
    }

    @Override
    public NotificationTemplateModel get(String id) {
        return notificationTemplateServiceClient.get(id);
    }

    @Override
    public NotificationTemplateModelList list(Integer page, Integer perPage, List<FindNotificationTemplatesQuery.NotificationTemplatesOrder> orders) {
        return notificationTemplateServiceClient.list(page, perPage, orders);
    }

    @Override
    public void delete(String id) {
        notificationTemplateServiceClient.delete(id);
    }

    @Override
    public List<NotificationTypeDescriptorModel> descriptors() {
        return notificationTypeDescriptorRegistry.descriptors();
    }

    @Override
    public NotificationTypeDescriptorModel descriptorByType(String type) {
        return notificationTypeDescriptorRegistry.byType(type).orElse(null);
    }
}
