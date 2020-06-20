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
public class ClientSocketChannel {

    private final MissionService missionService;

    //todo do not request user id when using security
    @MessageMapping("client.{clientId}")
    public Flux<MissionDto> updateClientLocationInActiveMission(final Flux<GeoPointDto> providerLocation,
                                                                @DestinationVariable("clientId") final String clientId) {
        return providerLocation.flatMap(location -> missionService.updateLocationOfClientInActiveMission(clientId, location));
    }

}
