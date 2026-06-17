package com.cl.mdd.server.core.data.persistent.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

@Converter(autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, Date> {

    @Override
    public Date convertToDatabaseColumn(ZonedDateTime attribute) {
        if (attribute == null) {
            return null;
        }
        return Date.from(attribute.withZoneSameInstant(ZoneOffset.UTC).toInstant());
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Date dbData) {
        if (dbData == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(dbData.toInstant(), ZoneId.of("UTC"));
    }
}
