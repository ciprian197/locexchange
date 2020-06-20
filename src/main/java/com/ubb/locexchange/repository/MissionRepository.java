package com.ubb.locexchange.repository;


import com.ubb.locexchange.domain.Mission;
import com.ubb.locexchange.domain.MissionStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface MissionRepository extends ReactiveMongoRepository<Mission, String> {

    Mono<Mission> findByProvider_IdAndStatus(String providerId, MissionStatus status);

    Mono<Mission> findByClient_IdAndStatus(String clientId, MissionStatus status);

}
