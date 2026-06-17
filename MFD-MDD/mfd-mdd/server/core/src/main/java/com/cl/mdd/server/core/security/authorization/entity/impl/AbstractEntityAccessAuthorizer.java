package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;
import com.cl.mdd.server.core.security.SecurityAccess;
import com.cl.mdd.server.core.security.authorization.entity.EntityAccessAuthorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * For {@link #logger} and {@link #securityAccess} re-use sake.
 */
public abstract class AbstractEntityAccessAuthorizer<T extends Identifiable> implements EntityAccessAuthorizer<T> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected SecurityAccess securityAccess;

}
