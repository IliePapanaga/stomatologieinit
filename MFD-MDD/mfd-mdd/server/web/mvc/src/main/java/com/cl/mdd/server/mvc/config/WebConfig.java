package com.cl.mdd.server.mvc.config;

import com.cl.mdd.server.core.config.CoreConfig;
import com.cl.mdd.server.jobs.config.JobSchedulingConfig;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@Import({CoreConfig.class, JobSchedulingConfig.class})
@ComponentScans({
        @ComponentScan("com.cl.mdd.server.mvc.rest")
})
//@EnableWebMvc // to keep spring-boot auto-configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return new JacksonConfig();
    }

    @Bean
    protected TypeResolver typeResolver() {
        return new TypeResolver();
    }


}
