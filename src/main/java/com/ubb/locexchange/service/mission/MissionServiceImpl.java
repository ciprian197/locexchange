package com.ubb.locexchange.service.mission;

import com.ubb.locexchange.domain.Mission;
import com.ubb.locexchange.domain.User;
import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.mission.MissionDto;
import com.ubb.locexchange.mapper.GeoPointMapper;
import com.ubb.locexchange.mapper.MissionMapper;
import com.ubb.locexchange.repository.MissionRepository;
import com.ubb.locexchange.service.user.UserInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.function.Consumer;

import static com.ubb.locexchange.domain.MissionStatus.IN_PROGRESS;

@Slf4j
@RequiredArgsConstructor
@Component
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;
    private final UserInternalService userService;
    private final GeoPointMapper geoPointMapper;
    private final MissionMapper missionMapper;

    @Override
    public Mono<MissionDto> updateLocationOfProviderInActiveMission(final String providerId, final GeoPointDto location) {
        return missionRepository.findByProvider_IdAndStatus(providerId, IN_PROGRESS)
                .flatMap(mission -> updateLocationInMission(mission, location, mission::setProviderLocation))
                .map(mission -> missionMapper.toDto(mission, geoPointMapper::toDto))
                .switchIfEmpty(Mono.just(new MissionDto()))
                .zipWith(userService.updateUserLocation(providerId, location))
                .map(Tuple2::getT1)
                .doOnNext(missionDto -> log.info(String.format("Successfully updated location of Provider(id=%s) in Mission(id = %s)",
                        providerId, missionDto.getId())));
    }

    @Override
    public Mono<MissionDto> updateLocationOfClientInActiveMission(final String clientId, final GeoPointDto location) {
        return missionRepository.findByClient_IdAndStatus(clientId, IN_PROGRESS)
                .flatMap(mission -> updateLocationInMission(mission, location, mission::setClientLocation))
                .switchIfEmpty(createMissionForClient(clientId, location))
                .map(mission -> missionMapper.toDto(mission, geoPointMapper::toDto))
                .doOnNext(missionDto -> log.info(String.format("Successfully updated location of Client(id=%s) in Mission(id = %s)",
                        clientId, missionDto.getId())));
    }


    private Mono<Mission> createMissionForClient(final String clientId, final GeoPointDto location) {
        return Mono.zip(userService.getUser(clientId), userService.getClosestAvailableProvider(location))
                .flatMap(clientAndProvider -> createMission(clientAndProvider.getT1(), clientAndProvider.getT2(), location));
    }

    private Mono<Mission> createMission(final User client, final User provider, final GeoPointDto clientLocationDto) {
        final GeoJsonPoint clientLocation = geoPointMapper.toEntity(clientLocationDto);
        final Mission mission = missionMapper.toEntity(client, provider, clientLocation);
        return missionRepository.save(mission);
    }

    private Mono<Mission> updateLocationInMission(final Mission mission, final GeoPointDto locationDto,
                                                  final Consumer<GeoJsonPoint> locationSetter) {
        final GeoJsonPoint location = geoPointMapper.toEntity(locationDto);
        locationSetter.accept(location);
        return missionRepository.save(mission);
    }

}
