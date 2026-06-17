package com.cl.mdd.server.core;

import com.cl.mdd.server.core.config.CoreConfig;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { CoreConfig.class, BeansForTesting.class })
//@DataJpaTest
@ActiveProfiles({"dev"})
public abstract class BaseIntegrationTest {

}
