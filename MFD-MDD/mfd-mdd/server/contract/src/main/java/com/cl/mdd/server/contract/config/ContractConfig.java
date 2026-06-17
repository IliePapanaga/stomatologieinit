package com.cl.mdd.server.contract.config;

import com.cl.mdd.server.contract.ContractsRegistry;
import com.cl.mdd.server.contract.Document;
import com.cl.mdd.server.contract.pdf.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class ContractConfig {

    @Value("classpath:MDD_PRACTICE.pdf")
    protected Resource practicePdfTemplate;

    @Value("classpath:MDD_PRO.pdf")
    protected Resource proPdfTemplate;

    @Bean
    public ContractsRegistry contractsRegistry() throws Exception {
        Template practice = new Template(practicePdfTemplate.getURL().toString());
        Template pro = new Template(proPdfTemplate.getURL().toString());
        return new ContractsRegistry() {
            @Override
            public Document professionalContract() {
                return pro;
            }

            @Override
            public Document practiceContract() {
                return practice;
            }
        };
    }


}
