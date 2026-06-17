package com.cl.mdd.server.core.event.impl.handler.posting.interview;

import com.cl.mdd.server.core.data.persistent.access.posting.JobInterviewDao;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.interview.JobInterviewFinishedEvent;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Consumer
public class MarkJobInterviewAboutFinishedEventHandler implements EventHandler<JobInterviewFinishedEvent> {

    @Autowired
    private JobInterviewDao jobInterviewDao;

    @Override
    @Transactional
    public void onEvent(JobInterviewFinishedEvent event, long sequence, boolean endOfBatch) {
        jobInterviewDao.markNotifiedAboutFinished(event.getInterviewId());
    }

}
