package com.cl.mdd.server.core.data.persistent.model.common;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface HasAbsoluteEndTime {

    LocalDate getEndDate();

    LocalTime getEndTime();
    
    ZoneId getTimeZone();

    void setZonedEndDateTime(ZonedDateTime zonedEndDateTime);

    ZonedDateTime getZonedEndDateTime();

}
