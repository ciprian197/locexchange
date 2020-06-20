package com.ubb.locexchange.service.mission;

import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.mission.MissionDto;
import reactor.core.publisher.Mono;

public interface MissionService {

    Mono<MissionDto> updateLocationOfProviderInActiveMission(String providerId, GeoPointDto location);

    Mono<MissionDto> updateLocationOfClientInActiveMission(String clientId, GeoPointDto location);

}
