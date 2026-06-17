package com.cl.mdd.server.core.config;

import com.cl.mdd.server.core.service.notification.definition.impl.NotificationBeanPostProcessor;
import com.cl.mdd.server.core.service.notification.definition.impl.NotificationTypeDescriptorRegistry;
import org.springframework.context.annotation.*;

/**
 * Notification integration base configuration
 */
@Configuration
@Import(MockNotificationClientConfig.class)
@ComponentScan(
    basePackages = {
            "com.cl.mdd.server.core.service.notification.impl"
    }
)
public class NotificationConfig {

    @Bean
    public NotificationTypeDescriptorRegistry notificationTypeDescriptorRegistry() {
        return new NotificationTypeDescriptorRegistry();
    }

    @Bean
    public NotificationBeanPostProcessor notificationBeanPostProcessor() {
        return new NotificationBeanPostProcessor(notificationTypeDescriptorRegistry());
    }
}
