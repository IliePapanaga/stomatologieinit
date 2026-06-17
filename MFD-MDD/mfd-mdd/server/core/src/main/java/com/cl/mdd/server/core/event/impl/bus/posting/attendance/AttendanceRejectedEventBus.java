package com.cl.mdd.server.core.event.impl.bus.posting.attendance;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifyEmployeeAboutRejectedAttendanceHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifySystemUserAboutRejectedAttendanceHandler;
import com.cl.mdd.server.core.event.type.posting.attendance.AttendanceRejectedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class AttendanceRejectedEventBus extends DisruptorEventBus<AttendanceRejectedEvent> {

    private static final int CONSUMER_STACK_SIZE = 3;

    @Autowired
    private NotifyEmployeeAboutRejectedAttendanceHandler notifyEmployeeAboutRejectedAttendanceHandler;

    @Autowired
    private NotifySystemUserAboutRejectedAttendanceHandler notifySystemUserAboutRejectedAttendanceHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<AttendanceRejectedEvent> initialize() {
        Disruptor<AttendanceRejectedEvent> disruptor = new Disruptor<>(AttendanceRejectedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyEmployeeAboutRejectedAttendanceHandler)
                .then(notifySystemUserAboutRejectedAttendanceHandler)
                .then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(AttendanceRejectedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getAttendanceRejectionId());
        Objects.requireNonNull(event.getJobPostingApplicationId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
