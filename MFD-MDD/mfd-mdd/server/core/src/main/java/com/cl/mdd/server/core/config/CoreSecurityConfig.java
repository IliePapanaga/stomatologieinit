package com.cl.mdd.server.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.encrypt.BouncyCastleAesCbcBytesEncryptor;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

public class CoreSecurityConfig {


    @Value("${crypt.key:mdd-default-pass}")
    private String cryptKey;

    @Value("${crypt.salt:37d8444854785d38}")
    private String cryptSalt;

    /**
     * For security context references in {@link @{@link org.springframework.data.jpa.repository.Query}} methods
     */
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Bean
    public TextEncryptor textEncryptor() {
        return new TextEncryptor() {
            private BouncyCastleAesCbcBytesEncryptor encryptor =
                    new BouncyCastleAesCbcBytesEncryptor(cryptKey, cryptSalt);

            @Override
            public String encrypt(String text) {
                return new String(Hex.encode(encryptor.encrypt(Utf8.encode(text))));
            }

            @Override
            public String decrypt(String encryptedText) {
                return Utf8.decode(encryptor.decrypt(Hex.decode(encryptedText)));
            }
        };
        // this would be much simpler, but 256-bit requires JCE packages
        // return Encryptors.queryableText(cryptKey, cryptSalt);
    }

}
