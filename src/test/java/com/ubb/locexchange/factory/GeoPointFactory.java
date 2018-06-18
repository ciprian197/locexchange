package com.ubb.locexchange.factory;

import com.ubb.locexchange.dto.GeoPointDto;

public class GeoPointFactory {

    public static GeoPointDto generateGeoPointDto() {
        return GeoPointDto.builder()
                .x(23.4567)
                .y(47.239).build();
    }

}
