package com.cl.mdd.server.core.service.notification.client.embedded;

import com.cl.mdd.server.core.data.model.notification.FindNotificationTemplatesQuery;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModel;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModelList;
import com.cl.mdd.server.core.service.notification.NotificationServiceException;
import com.cl.mdd.server.core.service.notification.client.NotificationTemplateServiceClient;
import com.cl.sns.server.core.model.api.template.BaseNotificationTemplateModel;
import com.cl.sns.server.core.model.api.template.CreateNotificationTemplate;
import com.cl.sns.server.core.service.NotificationTemplateService;
import com.cl.sns.server.core.service.common.SnsApplicationException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link NotificationTemplateServiceClient} that works with notification service deployed as a part of application
 */
@Service
public class EmbeddedNotificationTemplateServiceClient implements NotificationTemplateServiceClient {

    private final NotificationTemplateService notificationTemplateService;

    @Autowired
    public EmbeddedNotificationTemplateServiceClient(final NotificationTemplateService notificationTemplateService) {
        this.notificationTemplateService = notificationTemplateService;
    }

    @Override
    public NotificationTemplateModel save(NotificationTemplateModel template) {
        com.cl.sns.server.core.model.api.template.NotificationTemplateModel savedModel;

        if (StringUtils.isNotBlank(template.getId())) {
            savedModel = update(template);
        } else {
            savedModel = notificationTemplateService.save(toCreateModel(template));
        }

        return toClientModel(savedModel);
    }

    protected com.cl.sns.server.core.model.api.template.NotificationTemplateModel update(NotificationTemplateModel template) {
        try {
            return notificationTemplateService.update(toServiceModel(template));
        } catch (final SnsApplicationException ex) {
            throw new NotificationServiceException(ex.getMessage(), ex, ex.getCode());
        }
    }

    @Override
    public NotificationTemplateModel get(String id) {
        return toClientModel(notificationTemplateService.get(id));
    }

    @Override
    public NotificationTemplateModelList list(Integer page, Integer perPage, List<FindNotificationTemplatesQuery.NotificationTemplatesOrder> orders) {
        Pageable pageable = new PageRequest(page == null ? 0 : page, perPage == null ? 50 : perPage,
                buildSort(orders));

        com.cl.sns.server.core.model.api.template.NotificationTemplateModelList result = notificationTemplateService.list(pageable);

        return new NotificationTemplateModelList(result.getModels()
                .stream().map(this::toClientModel).collect(Collectors.toList()), result.getTotalCount());
    }

    protected Sort buildSort(List<FindNotificationTemplatesQuery.NotificationTemplatesOrder> orders) {
        if (CollectionUtils.isEmpty(orders)) {
            return null;
        }

        return new Sort(orders.stream()
                .map(this::convertToOrder)
                .collect(Collectors.toList()));
    }

    protected Sort.Order convertToOrder(FindNotificationTemplatesQuery.NotificationTemplatesOrder order) {
        return new Sort.Order(order.isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC, order.getPath());
    }

    @Override
    public void delete(String id) {
        notificationTemplateService.delete(id);
    }

    protected NotificationTemplateModel toClientModel(
            com.cl.sns.server.core.model.api.template.NotificationTemplateModel serviceModel) {
        return serviceModel == null ? null : new NotificationTemplateModel()
                .setId(serviceModel.getId())
                .setName(serviceModel.getName())
                .setDescription(serviceModel.getDescription())
                .setSubject(serviceModel.getSubject())
                .setContent(serviceModel.getContent())
                .setTransport(serviceModel.getTransport())
                .setType(serviceModel.getType());
    }

    protected com.cl.sns.server.core.model.api.template.NotificationTemplateModel toServiceModel(
            NotificationTemplateModel clientModel) {
        return withBaseProperties(clientModel, new com.cl.sns.server.core.model.api.template.NotificationTemplateModel())
                .setId(clientModel.getId());
    }

    protected CreateNotificationTemplate toCreateModel(NotificationTemplateModel clientModel) {
        return withBaseProperties(clientModel, new CreateNotificationTemplate());
    }

    protected <T extends BaseNotificationTemplateModel> T withBaseProperties(NotificationTemplateModel clientModel, T serviceModel) {
        return (T) serviceModel
                .setName(clientModel.getName())
                .setDescription(clientModel.getDescription())
                .setSubject(clientModel.getSubject())
                .setContent(clientModel.getContent())
                .setTransport(clientModel.getTransport())
                .setType(clientModel.getType());
    }
}
