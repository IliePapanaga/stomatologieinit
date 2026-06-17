package com.cl.mdd.server.core.data.persistent.listeners;

import com.cl.mdd.server.core.data.persistent.model.common.HasAbsoluteEndTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class AbsoluteEndTimeAdjuster {

    @PrePersist
    @PreUpdate
    public void populateAbsoluteEndTime(HasAbsoluteEndTime hasAbsoluteEndTime) {
        hasAbsoluteEndTime.setZonedEndDateTime(ZonedDateTime.of(LocalDateTime.of(hasAbsoluteEndTime.getEndDate(), hasAbsoluteEndTime.getEndTime()), hasAbsoluteEndTime.getTimeZone()));
    }

}
