package com.cl.mdd.server.core.service;

import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import com.cl.mdd.server.core.security.SecurityAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Supplier;

/**
 * For {@link #logger}, {@link #securityAccess}, {@link #commonConverter}, {@link #queryConverter} re-use sake.
 */
public abstract class ServiceSupport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected SecurityAccess securityAccess;

    @Autowired
    protected CommonConverter commonConverter;

    @Autowired
    protected QueryConverter queryConverter;

    @Autowired
    protected TransactionHelper transactionHelper;

    @Autowired
    protected UserDao userDao;

    public void executeInTransaction(Runnable runnable) {
        transactionHelper.executeInTransaction(runnable);
    }

    public <V> V executeInTransaction(Supplier<V> callable) {
        try {
            return transactionHelper.executeInTransaction(callable::get);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
