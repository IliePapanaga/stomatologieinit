package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.notification.FindNotificationTemplatesQuery;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModel;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModelList;
import com.cl.mdd.server.core.data.model.notification.NotificationTypeDescriptorModel;
import com.cl.mdd.server.core.security.annotation.RequiresSystemUserRole;
import com.cl.mdd.server.core.service.notification.NotificationTemplateService;
import com.cl.mdd.server.mvc.rest.graphql.model.Connection;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiresSystemUserRole
public class NotificationTemplateProvider implements GraphQLProvider {

    private final NotificationTemplateService notificationTemplateService;

    @Autowired
    public NotificationTemplateProvider(final NotificationTemplateService notificationTemplateService) {
        this.notificationTemplateService = notificationTemplateService;
    }

    @GraphQLQuery(name = "notificationTemplates")
    public Connection<NotificationTemplateModel> findAll(@GraphQLArgument(name = "page") Integer page,
                                                         @GraphQLArgument(name = "perPage") Integer perPage,
                                                         @GraphQLArgument(name = "orders") List<FindNotificationTemplatesQuery.NotificationTemplatesOrder> orders) {
        NotificationTemplateModelList result = notificationTemplateService.list(page, perPage, orders);
        return new Connection<>(result.getTemplates(), result.getTotalCount());
    }

    @GraphQLQuery(name = "notificationTemplate")
    public NotificationTemplateModel get(@GraphQLNonNull @GraphQLArgument(name = "id") String id) {
        return notificationTemplateService.get(id);
    }

    @GraphQLMutation(name = "updateNotificationTemplate")
    public NotificationTemplateModel update(@GraphQLArgument(name = "template") NotificationTemplateModel template) {
        return notificationTemplateService.save(template);
    }

    @GraphQLMutation(name = "addNotificationTemplate")
    public NotificationTemplateModel add(@GraphQLArgument(name = "template") NotificationTemplateModel template) {
        return notificationTemplateService.save(template);
    }

    @GraphQLMutation(name = "deleteNotificationTemplate")
    public void delete(@GraphQLNonNull @GraphQLArgument(name = "id") String id) {
        notificationTemplateService.delete(id);
    }

    @GraphQLQuery(name = "notificationTypes")
    public Connection<NotificationTypeDescriptorModel> findAllTypes() {
        List<NotificationTypeDescriptorModel> descriptors = notificationTemplateService.descriptors();
        return new Connection<>(descriptors, (long) descriptors.size());
    }

    @GraphQLQuery(name = "notificationType")
    public NotificationTypeDescriptorModel findType(@GraphQLNonNull @GraphQLArgument(name = "type") String type) {
        return notificationTemplateService.descriptorByType(type);
    }
}
