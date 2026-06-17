package com.cl.sns.server.core.config;

import com.cl.sns.server.core.service.msg.Messenger;
import com.cl.sns.server.core.service.msg.MessengerProvider;
import com.cl.sns.server.core.service.msg.content.MessageDetailsFactory;
import com.cl.sns.server.core.service.msg.content.MessageDetailsProviderImpl;
import com.cl.sns.server.core.service.msg.impl.MessengerProviderImpl;
import com.cl.sns.server.core.service.msg.mail.aws.AwsMailMessageDetailsFactory;
import com.cl.sns.server.core.service.msg.mail.aws.AwsMailMessenger;
import com.cl.sns.server.core.service.msg.sms.aws.AwsSmsMessageDetailsFactory;
import com.cl.sns.server.core.service.msg.sms.aws.AwsSmsMessenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Core SNS configuration file.
 * <p/>
 */
@Configuration
@Import(AWSConfig.class)
@ComponentScans({
        @ComponentScan("com.cl.sns.server.core"),
        @ComponentScan("com.cl.sns.server.bootstrap")
})
@EntityScan("com.cl.sns.server.core.model")
@EnableJpaRepositories("com.cl.sns.server.core.dao")
@EnableScheduling
@EnableAsync
@PropertySources({
        @PropertySource("classpath:sns-config.properties"),
})
public class CoreConfig {

    @Value("${email.transport.name}")
    private String emailTransportName;
    @Value("${sms.transport.name}")
    private String smsTransportName;
    @Autowired
    private AwsMailMessenger awsMailMessenger;
    @Autowired
    private AwsSmsMessenger awsSmsMessenger;
    @Autowired
    private AwsMailMessageDetailsFactory mailFactory;
    @Autowired
    private AwsSmsMessageDetailsFactory smsFactory;


    @Bean
    public MessengerProvider messengerProvider() {
        Map<String, Messenger> messengerRegistry = new HashMap<>();
        messengerRegistry.put(emailTransportName, awsMailMessenger);
        messengerRegistry.put(smsTransportName, awsSmsMessenger);

        return new MessengerProviderImpl(Collections.unmodifiableMap(messengerRegistry));
    }

    @Bean
    public MessageDetailsProviderImpl messageDetailsProvider() {
        Map<String, MessageDetailsFactory> registry = new HashMap<>();
        registry.put(awsMailMessenger.getClass().getSimpleName(), mailFactory);
        registry.put(awsSmsMessenger.getClass().getSimpleName(), smsFactory);

        return new MessageDetailsProviderImpl(Collections.unmodifiableMap(registry));
    }
}
