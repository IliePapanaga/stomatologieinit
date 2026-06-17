package com.cl.mdd.server.core.config;

import com.jaspersoft.jasperserver.jaxrs.client.core.JasperserverRestClient;
import com.jaspersoft.jasperserver.jaxrs.client.core.RestClientConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

public class ReportingConfig {

    @Value("${jasper.report.server.client.url}")
    private String url;

    @Value("${jasper.report.server.client.connectionTimeout}")
    private String connectionTimeout;

    @Value("${jasper.report.server.client.readTimeout}")
    private String readTimeout;

    @Value("${jasper.report.server.client.jasperserverVersion}")
    private String jasperserverVersion;

    @Value("${jasper.report.server.client.authenticationType}")
    private String authenticationType;

    @Value("${jasper.report.server.client.logHttp}")
    private String logHttp;

    @Value("${jasper.report.server.client.logHttpEntity}")
    private String logHttpEntity;

    @Value("${jasper.report.server.client.restrictedHttpMethods}")
    private String restrictedHttpMethods;

    @Value("${jasper.report.server.client.handleErrors}")
    private String handleErrors;

    @Value("${jasper.report.server.client.contentMimeType}")
    private String contentMimeType;

    @Value("${jasper.report.server.client.acceptMimeType}")
    private String acceptMimeType;

    @Bean
    public JasperserverRestClient jasperserverRestClient() {
        Properties properties = new Properties();
        properties.put("url", url);
        properties.put("connectionTimeout", connectionTimeout);
        properties.put("readTimeout", readTimeout);
        properties.put("jasperserverVersion", jasperserverVersion);
        properties.put("authenticationType", authenticationType);
        properties.put("logHttp", logHttp);
        properties.put("logHttpEntity", logHttpEntity);
        properties.put("restrictedHttpMethods", restrictedHttpMethods);
        properties.put("handleErrors", handleErrors);
        properties.put("contentMimeType", contentMimeType);
        properties.put("acceptMimeType", acceptMimeType);
        return new JasperserverRestClient(RestClientConfiguration.loadConfiguration(properties));
    }

}
