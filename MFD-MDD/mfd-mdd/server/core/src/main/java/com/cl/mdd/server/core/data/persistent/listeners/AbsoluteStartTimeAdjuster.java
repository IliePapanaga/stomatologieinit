package com.cl.mdd.server.core.data.persistent.listeners;

import com.cl.mdd.server.core.data.persistent.model.common.HasAbsoluteStartTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class AbsoluteStartTimeAdjuster {

    @PrePersist
    @PreUpdate
    public void populateAbsoluteStartTime(HasAbsoluteStartTime hasAbsoluteStartTime) {
        hasAbsoluteStartTime.setZonedStartDateTime(ZonedDateTime.of(LocalDateTime.of(hasAbsoluteStartTime.getStartDate(), hasAbsoluteStartTime.getStartTime()), hasAbsoluteStartTime.getTimeZone()));
    }


}
