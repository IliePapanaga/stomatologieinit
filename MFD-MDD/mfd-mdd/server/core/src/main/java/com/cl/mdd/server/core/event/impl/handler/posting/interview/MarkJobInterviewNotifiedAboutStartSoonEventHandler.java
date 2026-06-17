package com.cl.mdd.server.core.event.impl.handler.posting.interview;

import com.cl.mdd.server.core.data.persistent.access.posting.JobInterviewDao;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.interview.JobInterviewStartSoonEvent;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Consumer
public class MarkJobInterviewNotifiedAboutStartSoonEventHandler implements EventHandler<JobInterviewStartSoonEvent> {

    @Autowired
    private JobInterviewDao jobInterviewDao;

    @Override
    @Transactional
    public void onEvent(JobInterviewStartSoonEvent event, long sequence, boolean endOfBatch) {
        jobInterviewDao.markNotifiedAboutWorkStartSoon(event.getInterviewId());
    }

}
