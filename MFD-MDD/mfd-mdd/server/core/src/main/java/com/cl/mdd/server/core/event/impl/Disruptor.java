package com.cl.mdd.server.core.event.impl;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class Disruptor<T> extends com.lmax.disruptor.dsl.Disruptor<T> {

    public Disruptor(EventFactory<T> eventFactory, int ringBufferSize, Executor executor) {
        super(eventFactory, ringBufferSize, executor);
        this.setDefaultExceptionHandler(new IgnoreExceptionHandler());
    }

    public Disruptor(EventFactory<T> eventFactory, int ringBufferSize, Executor executor, ProducerType producerType, WaitStrategy waitStrategy) {
        super(eventFactory, ringBufferSize, executor, producerType, waitStrategy);
        this.setDefaultExceptionHandler(new IgnoreExceptionHandler());
    }

    public Disruptor(EventFactory<T> eventFactory, int ringBufferSize, ThreadFactory threadFactory) {
        super(eventFactory, ringBufferSize, threadFactory);
        this.setDefaultExceptionHandler(new IgnoreExceptionHandler());
    }

    public Disruptor(EventFactory<T> eventFactory, int ringBufferSize, ThreadFactory threadFactory, ProducerType producerType, WaitStrategy waitStrategy) {
        super(eventFactory, ringBufferSize, threadFactory, producerType, waitStrategy);
        this.setDefaultExceptionHandler(new IgnoreExceptionHandler());
    }
}
