package com.cl.mdd.server.core.event.impl.bus.posting.attendance;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.MarkJobDayNotifiedAboutWorkStartedHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifyEmployeeAboutWorkStartedHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifySystemAboutWorkStartedHandler;
import com.cl.mdd.server.core.event.type.posting.attendance.WorkStartedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class WorkStartedEventBus extends DisruptorEventBus<WorkStartedEvent> {

    private static final int CONSUMER_STACK_SIZE = 4;

    @Autowired
    private NotifyEmployeeAboutWorkStartedHandler notifyEmployeeAboutWorkStartedHandler;

    @Autowired
    private NotifySystemAboutWorkStartedHandler notifySystemAboutWorkStartedHandler;

    @Autowired
    private MarkJobDayNotifiedAboutWorkStartedHandler markJobDayNotifiedAboutWorkStartedHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<WorkStartedEvent> initialize() {
        Disruptor<WorkStartedEvent> disruptor = new Disruptor<>(WorkStartedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyEmployeeAboutWorkStartedHandler)
                .then(notifySystemAboutWorkStartedHandler)
                .then(markJobDayNotifiedAboutWorkStartedHandler)
                .then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(WorkStartedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getJobDayId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
