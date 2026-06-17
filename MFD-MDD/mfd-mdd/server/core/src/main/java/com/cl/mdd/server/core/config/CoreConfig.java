package com.cl.mdd.server.core.config;

import com.cl.mdd.server.core.data.persistent.listeners.SpringSecurityAuditorAware;
import org.apache.tika.Tika;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

@Configuration
@Import({CoreSecurityConfig.class, ReportingConfig.class})
@ComponentScan("com.cl.mdd.server.core")
@EntityScan(basePackages = {"com.cl.mdd.server.core.data.persistent.model", "org.springframework.data.jpa.convert.threeten", "com.cl.mdd.server.core.data.persistent.converter"})
@EnableJpaRepositories("com.cl.mdd.server.core.data.persistent.access")
@EnableJpaAuditing
//@EnableScheduling
@EnableAsync
@EnableCaching
@EnableAspectJAutoProxy
public class CoreConfig {

    /**
     * Value provider for @CreatedBy, @LastModifiedBy annotated fields.
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringSecurityAuditorAware();
    }

    /**
     * For security context references in {@link @{@link org.springframework.data.jpa.repository.Query}} methods
     */
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    /**
     * http://tika.apache.org/1.17/detection.html
     */
    @Bean
    public Tika tika() {
        return new Tika();
    }

}
