package com.cl.mdd.server.core.event.impl.bus.posting.attendance;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifyEmployeeAboutNoShowAttendanceHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifySystemUserAboutNoShowAttendanceHandler;
import com.cl.mdd.server.core.event.type.posting.attendance.AttendanceNoShowEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class AttendanceNoShowEventBus extends DisruptorEventBus<AttendanceNoShowEvent> {

    private static final int CONSUMER_STACK_SIZE = 3;

    @Autowired
    private NotifyEmployeeAboutNoShowAttendanceHandler notifyEmployeeAboutNoShowAttendanceHandler;

    @Autowired
    private NotifySystemUserAboutNoShowAttendanceHandler notifySystemUserAboutNoShowAttendanceHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<AttendanceNoShowEvent> initialize() {
        Disruptor<AttendanceNoShowEvent> disruptor = new Disruptor<>(AttendanceNoShowEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyEmployeeAboutNoShowAttendanceHandler)
                .then(notifySystemUserAboutNoShowAttendanceHandler)
                .then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(AttendanceNoShowEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getNoShowId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
