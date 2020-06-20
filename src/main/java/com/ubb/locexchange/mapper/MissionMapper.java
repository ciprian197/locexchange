package com.ubb.locexchange.mapper;

import com.ubb.locexchange.domain.Mission;
import com.ubb.locexchange.domain.MissionStatus;
import com.ubb.locexchange.domain.User;
import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.mission.MissionDto;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class MissionMapper {

    public MissionDto toDto(final Mission mission, final Function<GeoJsonPoint, GeoPointDto> toGeoPointDto) {
        return MissionDto.builder()
                .id(mission.getId())
                .providerId(mission.getProvider().getId())
                .clientId(mission.getClient().getId())
                .missionStatus(mission.getStatus())
                .providerLocation(toGeoPointDto.apply(mission.getProviderLocation()))
                .clientLocation(toGeoPointDto.apply(mission.getClientLocation())).build();
    }

    public Mission toEntity(final User client, final User provider, final GeoJsonPoint clientLocation) {
        return Mission.builder()
                .client(client)
                .provider(provider)
                .clientLocation(clientLocation)
                .providerLocation(provider.getLocation())
                .status(MissionStatus.IN_PROGRESS).build();
    }

}
