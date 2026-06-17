package com.cl.mdd.server.deploy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication(
        scanBasePackages = "com.cl.mdd.server.mvc.config"
)
public class MDDBootApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MDDBootApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MDDBootApplication.class);
    }

}
