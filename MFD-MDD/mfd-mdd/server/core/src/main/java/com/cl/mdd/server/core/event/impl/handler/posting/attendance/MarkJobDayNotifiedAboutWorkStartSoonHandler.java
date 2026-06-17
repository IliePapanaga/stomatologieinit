package com.cl.mdd.server.core.event.impl.handler.posting.attendance;

import com.cl.mdd.server.core.data.persistent.access.posting.JobDayDao;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.attendance.WorkStartSoonEvent;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Consumer
public class MarkJobDayNotifiedAboutWorkStartSoonHandler implements EventHandler<WorkStartSoonEvent> {

    @Autowired
    private JobDayDao jobDayDao;

    @Override
    @Transactional
    public void onEvent(WorkStartSoonEvent event, long sequence, boolean endOfBatch) {
        jobDayDao.markNotifiedAboutWorkStartSoon(event.getJobDayId());
    }

}
