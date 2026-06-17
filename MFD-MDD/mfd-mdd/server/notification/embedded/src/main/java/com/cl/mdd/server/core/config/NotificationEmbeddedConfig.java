package com.cl.mdd.server.core.config;

import com.cl.sns.server.core.config.CoreConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration for notification service client for embedded mode - when notification service is deployed as a part of application
 */
@Configuration
@ComponentScan(basePackages = {
        "com.cl.mdd.server.core.service.notification.client.embedded"
})
@Import(CoreConfig.class)
public class NotificationEmbeddedConfig {
}
