package com.cl.sns.server.mvc;

import com.cl.sns.server.mvc.config.WebConfig;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = WebConfig.class)
@ComponentScan(basePackages = "com.cl.sns.server.mvc.rest.controller.mapper")
public abstract class SNSBaseIntegrationTest {

}
