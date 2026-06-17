package com.cl.mdd.server.core.event.impl.bus.posting.attendance;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.MarkJobDayNotifiedAboutWorkStartSoonHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifyEmployeeAboutWorkStartSoonHandler;
import com.cl.mdd.server.core.event.type.posting.attendance.WorkStartSoonEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class WorkStartSoonEventBus extends DisruptorEventBus<WorkStartSoonEvent> {

    private static final int CONSUMER_STACK_SIZE = 3;

    @Autowired
    private NotifyEmployeeAboutWorkStartSoonHandler notifyEmployeeAboutWorkStartSoonHandler;

    @Autowired
    private MarkJobDayNotifiedAboutWorkStartSoonHandler markJobDayNotifiedAboutWorkStartSoonHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<WorkStartSoonEvent> initialize() {
        Disruptor<WorkStartSoonEvent> disruptor = new Disruptor<>(WorkStartSoonEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyEmployeeAboutWorkStartSoonHandler)
                .then(markJobDayNotifiedAboutWorkStartSoonHandler)
                .then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(WorkStartSoonEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getJobDayId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
