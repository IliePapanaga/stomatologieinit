package com.cl.sns.server.core.dao;

import com.cl.sns.server.core.SNSBaseIntegrationTest;
import com.cl.sns.server.core.model.db.msg.MessagingTransport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class MessagingTransportDaoIT extends SNSBaseIntegrationTest {
    @Autowired
    private MessagingTransportDao messagingTransportDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void CRUD() throws Exception {
        String name = "MESSAGING_TRANSPORT";
        String updatedName = name + "_UPDATED";
        MessagingTransport messagingTransport = new MessagingTransport(name);
        messagingTransportDao.save(messagingTransport);

        String messagingTransportId = messagingTransport.getId();
        Assert.assertNotNull(messagingTransportId);

        entityManager.flush();
        entityManager.detach(messagingTransport);

        MessagingTransport dbTransport = messagingTransportDao.findOne(messagingTransportId);

        // update
        dbTransport.setName(updatedName);
        messagingTransportDao.save(dbTransport);

        entityManager.flush();
        entityManager.detach(messagingTransport);

        dbTransport = messagingTransportDao.findOne(messagingTransportId);

        Assert.assertEquals(updatedName, dbTransport.getName());

        // delete
        messagingTransportDao.delete(messagingTransportId);
        entityManager.flush();
        entityManager.detach(messagingTransport);

        Assert.assertNull(messagingTransportDao.findOne(messagingTransportId));
    }
    @Test
    public void findByName() throws Exception {
        String name = "MESSAGING_TRANSPORT";
        MessagingTransport messagingTransport = new MessagingTransport(name);
        messagingTransportDao.save(messagingTransport);


        entityManager.flush();
        entityManager.detach(messagingTransport);

        MessagingTransport byName = messagingTransportDao.findByName(name);

        Assert.assertNotNull(byName);
    }
}