package com.cl.mdd.server.core.event.impl.bus.posting.attendance;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifySystemAboutAttendanceSosRequestedHandler;
import com.cl.mdd.server.core.event.type.posting.attendance.AttendanceSosRequestedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class AttendanceSosRequestedEventBus extends DisruptorEventBus<AttendanceSosRequestedEvent> {

    private static final int CONSUMER_STACK_SIZE = 2;

    @Autowired
    private NotifySystemAboutAttendanceSosRequestedHandler notifySystemAboutAttendanceSosRequestedHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<AttendanceSosRequestedEvent> initialize() {
        Disruptor<AttendanceSosRequestedEvent> disruptor = new Disruptor<>(AttendanceSosRequestedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifySystemAboutAttendanceSosRequestedHandler).then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(AttendanceSosRequestedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getSosRequestId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
