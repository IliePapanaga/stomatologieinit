package com.cl.mdd.server.core.event.impl.bus.posting.attendance;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifyEmployeeAboutCheckedInAttendanceHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifySystemUserAboutCheckedInAttendanceHandler;
import com.cl.mdd.server.core.event.type.posting.attendance.AttendanceCheckedInEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class AttendanceCheckedInEventBus extends DisruptorEventBus<AttendanceCheckedInEvent> {

    private static final int CONSUMER_STACK_SIZE = 3;

    @Autowired
    private NotifyEmployeeAboutCheckedInAttendanceHandler notifyEmployeeAboutCheckedInAttendanceHandler;

    @Autowired
    private NotifySystemUserAboutCheckedInAttendanceHandler notifySystemUserAboutCheckedInAttendanceHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<AttendanceCheckedInEvent> initialize() {
        Disruptor<AttendanceCheckedInEvent> disruptor = new Disruptor<>(AttendanceCheckedInEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyEmployeeAboutCheckedInAttendanceHandler)
                .then(notifySystemUserAboutCheckedInAttendanceHandler)
                .then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(AttendanceCheckedInEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getCheckInId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
