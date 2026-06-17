package com.cl.mdd.server.core.data.persistent.converter;

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ZonedDateTimeConverterTest {

    @Spy
    private ZonedDateTimeConverter ZonedDateTimeConverter;

    @Test
    public void test() {
        IntStream.range(1, 12).forEach(diff -> {
            ZonedDateTime randomOffsetFromUtc = ZonedDateTime.now(ZoneId.of("UTC+" + diff));
            randomOffsetFromUtc = randomOffsetFromUtc.truncatedTo(ChronoUnit.MILLIS);

            ZonedDateTime zonedDateTime = ZonedDateTimeConverter.convertToEntityAttribute(ZonedDateTimeConverter.convertToDatabaseColumn(randomOffsetFromUtc));
            zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.MILLIS);

            assertThat(randomOffsetFromUtc.withZoneSameInstant(ZoneId.of("UTC")), IsEqual.equalTo(zonedDateTime));
        });
    }
}