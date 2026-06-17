package com.cl.mdd.server.core.settings.converter;

import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.settings.SettingType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class SettingValueProcessorTest {
    
    private SettingValueProcessor processor;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        processor = new SettingValueProcessor();
    }

    @Test
    public void deserialize_whenToBoolean_returnBoolean() {
        Object actual = processor.deserialize(SettingType.BOOLEAN, "true");

        assertEquals(Boolean.TRUE, actual);
    }

    @Test
    public void deserialize_whenToString_returnString() {
        Object actual = processor.deserialize(SettingType.STRING, "some_String");

        assertEquals("some_String", actual);
    }

    @Test
    public void deserialize_whenToLong_returnLong() {
        Object actual = processor.deserialize(SettingType.LONG, "12345");

        assertEquals(new Long(12345), actual);
    }

    @Test
    public void deserialize_whenToLongCannotBeConverted_throwException() {
        expectedException.expect(MDDException.class);

        processor.deserialize(SettingType.LONG, "not_long");
    }

    @Test
    public void deserialize_whenToDate_returnDate() {
        Object actual = processor.deserialize(SettingType.DATE, "2017-02-12");

        assertEquals(LocalDate.parse("2017-02-12"), actual);
    }

    @Test
    public void deserialize_whenToDateCannotBeConverted_throwException() {
        expectedException.expect(MDDException.class);

        processor.deserialize(SettingType.DATE, "not_date");
    }
}