package com.cl.sns.server.core.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AWS configuration file.
 * <p />
 */
@Configuration
public class AWSConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${aws.ses.access.key}")
    private String awsSesAccessKey;
    @Value("${aws.ses.secret.key}")
    private String awsSesSecretKey;
    @Value("${aws.ses.region}")
    private String awsSesRegion;

    @Value("${aws.sns.access.key}")
    private String awsSnsAccessKey;
    @Value("${aws.sns.secret.key}")
    private String awsSnsSecretKey;
    @Value("${aws.sns.region}")
    private String awsSnsRegion;

    @Bean
    public AmazonSimpleEmailService awsSimpleEmailService() {
        logger.debug("initializing SES, access key {}, secret ...{}",
                awsSesAccessKey, StringUtils.substring(awsSesSecretKey, StringUtils.length(awsSesSecretKey) - 4));
        AWSCredentials awsSesCredentials = new BasicAWSCredentials(awsSesAccessKey, awsSesSecretKey);
        Regions region = Regions.fromName(awsSesRegion);

        return AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsSesCredentials))
                .withRegion(region)
                .build();
    }

    @Bean
    public AmazonSNS snsClient() {
        logger.debug("initializing SNS, access key {}, secret ...{}",
                awsSnsAccessKey, StringUtils.substring(awsSnsSecretKey, StringUtils.length(awsSnsSecretKey) - 4));
        AWSCredentials awsSnsCredentials = new BasicAWSCredentials(awsSnsAccessKey, awsSnsSecretKey);
        Regions region = Regions.fromName(awsSnsRegion);

        return AmazonSNSClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsSnsCredentials))
                .build();

    }


}
