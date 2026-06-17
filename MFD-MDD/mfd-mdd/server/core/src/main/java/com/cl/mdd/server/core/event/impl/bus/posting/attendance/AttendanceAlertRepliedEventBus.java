package com.cl.mdd.server.core.event.impl.bus.posting.attendance;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifyEmployerAboutRepliedAttendanceAlertHandler;
import com.cl.mdd.server.core.event.type.posting.attendance.AttendanceAlertRepliedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class AttendanceAlertRepliedEventBus extends DisruptorEventBus<AttendanceAlertRepliedEvent> {

    private static final int CONSUMER_STACK_SIZE = 2;

    @Autowired
    private NotifyEmployerAboutRepliedAttendanceAlertHandler notifyEmployerAboutRepliedAttendanceAlertHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<AttendanceAlertRepliedEvent> initialize() {
        Disruptor<AttendanceAlertRepliedEvent> disruptor = new Disruptor<>(AttendanceAlertRepliedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyEmployerAboutRepliedAttendanceAlertHandler).then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(AttendanceAlertRepliedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getAttendanceAlertId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
