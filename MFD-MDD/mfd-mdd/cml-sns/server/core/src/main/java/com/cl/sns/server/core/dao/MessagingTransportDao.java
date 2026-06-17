package com.cl.sns.server.core.dao;

import com.cl.sns.server.core.model.db.msg.MessagingTransport;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MessagingTransportDao extends JpaRepository<MessagingTransport, String> {
    MessagingTransport findByName(String name);
}
