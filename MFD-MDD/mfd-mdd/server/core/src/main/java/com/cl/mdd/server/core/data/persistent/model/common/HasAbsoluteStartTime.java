package com.cl.mdd.server.core.data.persistent.model.common;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface HasAbsoluteStartTime {

    LocalDate getStartDate();

    LocalTime getStartTime();

    ZoneId getTimeZone();

    void setZonedStartDateTime(ZonedDateTime zonedStartDateTime);

    ZonedDateTime getZonedStartDateTime();

}
