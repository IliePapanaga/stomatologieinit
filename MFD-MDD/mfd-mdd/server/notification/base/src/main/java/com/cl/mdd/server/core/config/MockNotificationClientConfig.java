package com.cl.mdd.server.core.config;

import com.cl.mdd.server.core.service.notification.client.NotificationServiceClient;
import com.cl.mdd.server.core.service.notification.client.mock.MockNotificationServiceClient;
import com.cl.mdd.server.core.service.notification.client.mock.MockNotificationTemplateServiceClient;
import org.springframework.context.annotation.*;

/**
 * Configuration for Mock notifications enabled by "dev" profile
 */
@Configuration
@ComponentScan(
        basePackages = {
                "com.cl.mdd.server.core.service.notification.client.mock"
        }
)
@Profile("mock-notification-service")
public class MockNotificationClientConfig {

    @Bean
    @Primary
    public NotificationServiceClient mockNotificationServiceClient() {
        return new MockNotificationServiceClient();
    }

    @Bean
    @Primary
    public MockNotificationTemplateServiceClient mockNotificationTemplateServiceClient() {
        return new MockNotificationTemplateServiceClient();
    }
}
