package com.cl.sns.server.core;

import com.cl.sns.server.core.config.CoreConfig;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = CoreConfig.class)
@ComponentScan(basePackages = "com.cl.sns")
public abstract class SNSBaseIntegrationTest {

}
