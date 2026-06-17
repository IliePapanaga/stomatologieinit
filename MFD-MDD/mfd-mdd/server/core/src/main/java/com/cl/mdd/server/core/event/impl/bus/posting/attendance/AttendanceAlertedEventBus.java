package com.cl.mdd.server.core.event.impl.bus.posting.attendance;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifyEmployeeAboutAlertedAttendanceHandler;
import com.cl.mdd.server.core.event.type.posting.attendance.AttendanceAlertedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class AttendanceAlertedEventBus extends DisruptorEventBus<AttendanceAlertedEvent> {

    private static final int CONSUMER_STACK_SIZE = 2;

    @Autowired
    private NotifyEmployeeAboutAlertedAttendanceHandler notifyEmployeeAboutAlertedAttendanceHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<AttendanceAlertedEvent> initialize() {
        Disruptor<AttendanceAlertedEvent> disruptor = new Disruptor<>(AttendanceAlertedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyEmployeeAboutAlertedAttendanceHandler).then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(AttendanceAlertedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getAlertId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
