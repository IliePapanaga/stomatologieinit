package com.cl.mdd.server.core.security;

import com.cl.mdd.server.core.BaseIntegrationTest;
import com.cl.mdd.server.core.config.CoreSecurityConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = CoreSecurityConfig.class)
public class EncryptionIT extends BaseIntegrationTest {

    @Autowired
    private TextEncryptor encryptor;

    @Test
    public void testEncryptionConfig() {
        String randomAlphanumeric = RandomStringUtils.randomAlphanumeric(50);
        assertEquals(randomAlphanumeric, encryptor.decrypt(encryptor.encrypt(randomAlphanumeric)));
    }

}
