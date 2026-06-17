package com.cl.mdd.server.core.service.geocoding;

import com.cl.mdd.server.core.data.model.geocoding.GeocodingArea;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeocodingUtilsTest {

    private GeocodingUtils utils = new GeocodingUtils();

    @Test
    public void toSquaredArea() throws Exception {
        double distance = 10d;
        double lat = 28.814478d;
        double lng = 47.036994d;
        GeocodingArea area = utils.toSquaredArea(distance, lat, lng);
        assertNotNull(area);
        assertTrue(distance == area.getDistance());
        assertTrue(lat == area.getLat());
        assertTrue(lng == area.getLng());
        assertTrue(28.669550463768118 == area.getLat1());
        assertTrue(28.959405536231884 == area.getLat2());
        assertTrue(46.87158652356356 == area.getLng1());
        assertTrue(47.20240147643644 == area.getLng2());
    }

    @Test
    public void toSquaredAreaWithNullDistance() throws Exception {
        GeocodingArea area = utils.toSquaredArea(null, 28.814478d, 47.036994d);
        assertEmptyResponse(area);
    }

    @Test
    public void toSquaredAreaWithNullLat() throws Exception {
        GeocodingArea area = utils.toSquaredArea(100d, null, 47.036994d);
        assertEmptyResponse(area);
    }
    @Test
    public void toSquaredAreaWithNullLng() throws Exception {
        GeocodingArea area = utils.toSquaredArea(100d, 47.036994d, null);
        assertEmptyResponse(area);
    }

    private void assertEmptyResponse(GeocodingArea area) {
        assertNotNull(area);
        assertNull(area.getDistance());
        assertNull(area.getLat());
        assertNull(area.getLng());
        assertNull(area.getLat1());
        assertNull(area.getLat2());
        assertNull(area.getLng1());
        assertNull(area.getLng2());
    }

}