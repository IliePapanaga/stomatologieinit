package com.cl.mdd.server.core.service.payment.impl.primerate;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class GatewayClientConfig {

//    @Bean
//    public HttpClient primerateHttpClient() {
//        return HttpClientBuilder.create().build();
//    }

    @Bean
    public RestTemplate primerateRestTemplate(RestTemplateBuilder builder/*, HttpClient client*/) {
//        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        Jaxb2RootElementHttpMessageConverter converter = new Jaxb2RootElementHttpMessageConverter();
        List<MediaType> types = new ArrayList<>(converter.getSupportedMediaTypes().size() + 1);
        types.addAll(converter.getSupportedMediaTypes());
        types.add(MediaType.TEXT_HTML);
        converter.setSupportedMediaTypes(types);
        return builder.defaultMessageConverters().additionalMessageConverters(converter).build();
    }
}
