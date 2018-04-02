package com.ubb.locexchange.mapper;

import com.ubb.locexchange.dto.GeoPointDto;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

@Component
public class GeoPointMapper {

    public GeoJsonPoint toEntity(final GeoPointDto dto) {
        if (dto == null) {
            return null;
        }

        return new GeoJsonPoint(dto.getX(), dto.getY());
    }

    public GeoPointDto toDto(final GeoJsonPoint entity) {
        if (entity == null) {
            return null;
        }

        return GeoPointDto.builder()
                .x(entity.getX())
                .y(entity.getY()).build();
    }

    public Point toPoint(final GeoPointDto dto) {
        if (dto == null) {
            return null;
        }

        return new Point(dto.getX(), dto.getY());
    }

}
