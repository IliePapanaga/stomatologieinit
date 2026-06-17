package com.cl.mdd.server.core.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;

@Service
public class TransactionHelper {

    @Transactional
    public void executeInTransaction(Runnable runnable) {
        runnable.run();
    }

    @Transactional
    public <V> V executeInTransaction(Callable<V> callable) throws Exception {
        return callable.call();
    }
}