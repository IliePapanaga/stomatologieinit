package com.cl.sns.server.bootstrap;

import com.cl.sns.server.core.dao.MessagingTransportDao;
import com.cl.sns.server.core.model.db.msg.MessagingTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * SNS data loader.
 * <p />
 * Init database with default data set.
 */
@Component
public class SnsDataLoader implements ApplicationRunner {

    @Value("${email.transport.name}")
    private String emailTransportName;
    @Value("${sms.transport.name}")
    private String smsTransportName;
    @Value("${db.initializer.enabled:true}")
    private Boolean enabled;

    @Autowired
    private MessagingTransportDao messagingTransportDao;

    @Override
    public void run(ApplicationArguments args) throws Exception {
       if(Boolean.TRUE.equals(enabled)){
           persistTransport(emailTransportName);
           persistTransport(smsTransportName);
        }
    }

    protected void persistTransport(String transportName){
        MessagingTransport dbTransport = messagingTransportDao.findByName(transportName);
        if(dbTransport == null){
            messagingTransportDao.save(new MessagingTransport(transportName));
        }
    }

}
