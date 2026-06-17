package com.cl.mdd.server.core.data.persistent.access.contact;

import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.contact.Contact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactDao extends AbstractDao<Contact> {

    @Query("select u.contact from User u where u.id = ?1")
    Contact findUserContact(String userId);
}
