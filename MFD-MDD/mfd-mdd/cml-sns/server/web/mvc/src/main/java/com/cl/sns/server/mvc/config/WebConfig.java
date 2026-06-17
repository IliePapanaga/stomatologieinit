package com.cl.sns.server.mvc.config;

import com.cl.sns.server.core.config.CoreConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@Import({CoreConfig.class})
@ComponentScans({
        @ComponentScan("com.cl.sns.server.mvc.rest")
})
public class WebConfig extends WebMvcConfigurerAdapter {

}
