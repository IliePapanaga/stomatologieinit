package com.cl.mdd.server.core;

import com.cl.mdd.server.core.data.persistent.access.payment.PaymentDao;
import com.cl.mdd.server.core.data.persistent.access.posting.CheckInDao;
import com.cl.mdd.server.core.data.persistent.access.posting.JobDayDao;
import com.cl.mdd.server.core.data.persistent.access.posting.JobInterviewDao;
import com.cl.mdd.server.core.data.persistent.access.posting.PermanentJobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.manager.payment.PaymentManager;
import com.cl.mdd.server.core.security.SecurityAccess;
import com.cl.mdd.server.core.settings.SystemSettings;
import org.apache.http.HttpClientConnection;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Configuration
public class BeansForTesting {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityAccess webSecurityAccess() {
        return new SecurityAccess();
    }

    @Bean
    public CacheManager cacheManager() {
        return new NoOpCacheManager();
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClientBuilder.create()
                /*.setRetryHandler(new DefaultHttpRequestRetryHandler(2, true, Collections.singleton(NoHttpResponseException.class)) {
                    @Override
                    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                        boolean result = super.retryRequest(exception, executionCount, context);
                        LoggerFactory.getLogger(BeansForTesting.class.getName() + ".httpClient")
                                .warn("retrying {}, count {}", exception.getClass(), executionCount);
                        return result;
                    }
                })*/
                .setConnectionManager(new PoolingHttpClientConnectionManager() {
                    @Override
                    public void releaseConnection(HttpClientConnection managedConn, Object state, long keepalive, TimeUnit tunit) {
                        super.releaseConnection(managedConn, state, 1, tunit);
                    }
                })
                .build();
    }

    @Bean
    @Primary
    public PaymentManager paymentManager(
            CheckInDao checkInDao, JobInterviewDao jobInterviewDao, PermanentJobPostingApplicationDao applicationDao,
            PaymentDao paymentDao, SystemSettings systemSettings) {
        return new PaymentManager(checkInDao, jobInterviewDao, applicationDao, paymentDao, systemSettings) {
            @Override
            protected BigDecimal rate(SubCategory subCategory) {
                return super.rate(subCategory).add(
                        new BigDecimal(new Random().nextInt(1000) / 100.0))
                        .setScale(2, RoundingMode.HALF_DOWN);
            }
        };
    }

}
