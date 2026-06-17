package com.cl.mdd.server.core.service.geocoding;

import com.cl.mdd.server.core.data.model.geocoding.GeocodingArea;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import static java.lang.Math.*;

/**
 * Geocoding utils.
 * <p/>
 *
 */
@Service
public class GeocodingUtils {
    public static final double ONE_DEGREE_IN_MILES = 69;


    /**
     * Calculates geocoding area for the given distance, lat and lng.
     * <p/>
     * @param distance
     * @param lat
     * @param lng
     * @return the calculated area in case when all arguments are non null, otherwise an empty geolocation area instance.
     */
    public GeocodingArea toSquaredArea(Double distance, Double lat, Double lng) {
        GeocodingArea geocodingArea = new GeocodingArea();

        if (ObjectUtils.allNotNull(distance, lat, lng)) {
            geocodingArea.setDistance(distance)
                    .setLat(lat)
                    .setLng(lng)
                    .setLng1(lng - distance / abs(cos(toRadians(lat)) * ONE_DEGREE_IN_MILES))
                    .setLng2(lng + distance / abs(cos(toRadians(lat)) * ONE_DEGREE_IN_MILES))
                    .setLat1(lat - (distance / ONE_DEGREE_IN_MILES))
                    .setLat2(lat + (distance / ONE_DEGREE_IN_MILES));
        }
        return geocodingArea;
    }
}
