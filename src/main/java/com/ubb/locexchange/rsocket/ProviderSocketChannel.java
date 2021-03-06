package com.ubb.locexchange.rsocket;

import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.mission.MissionDto;
import com.ubb.locexchange.service.mission.MissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ProviderSocketChannel {

    private final MissionService missionService;

    //todo do not request user id when using security
    @MessageMapping("provider.{providerId}")
    public Flux<MissionDto> updateProviderLocationInMission(final Flux<GeoPointDto> providerLocation,
                                                            @DestinationVariable("providerId") final String providerId) {
        return providerLocation.flatMap(location -> missionService.updateLocationOfProviderInActiveMission(providerId, location));
    }

}
