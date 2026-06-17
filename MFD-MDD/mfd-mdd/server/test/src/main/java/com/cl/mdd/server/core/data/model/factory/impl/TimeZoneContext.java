package com.cl.mdd.server.core.data.model.factory.impl;

import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class TimeZoneContext {

    private final AtomicReference<ZoneId> timeZone = new AtomicReference<>();

    public ZoneId get() {
        ZoneId zoneId = timeZone.get();

        if (zoneId == null) {
            synchronized (timeZone) {
                if (timeZone.get() == null) {
                    ArrayList<String> timeZones = new ArrayList<>(ZoneId.getAvailableZoneIds());
                    timeZones.remove("GMT0");
                    Collections.shuffle(timeZones);
                    timeZone.set(ZoneId.of(timeZones.iterator().next()));
                }
            }
        }

        return timeZone.get();
    }

    public ZonedDateTime zonedDateTime(){
        return ZonedDateTime.now().withZoneSameInstant(get());
    }

}
