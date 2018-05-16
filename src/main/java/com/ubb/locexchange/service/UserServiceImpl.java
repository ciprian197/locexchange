package com.ubb.locexchange.service;

import com.ubb.locexchange.controller.exception.ResourceNotFoundExcetion;
import com.ubb.locexchange.domain.User;
import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.UserDto;
import com.ubb.locexchange.mapper.GeoPointMapper;
import com.ubb.locexchange.mapper.UserMapper;
import com.ubb.locexchange.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

import static com.ubb.locexchange.controller.exception.ErrorType.CANNOT_FIND_AVAILABLE_PROVIDERS;
import static com.ubb.locexchange.domain.Role.PROVIDER;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final int MAXIMUM_QUERY_RESULTS = 5;

    private final Distance maxDistance;
    private final UserMapper userMapper;
    private final GeoPointMapper geoPointMapper;
    private final UserRepository userRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final DistanceExternalService distanceExternalService;

    public UserServiceImpl(@Value("${distance.maximum.allowed}") final double maxDistance,
                           final UserMapper userMapper, final GeoPointMapper geoPointMapper,
                           final UserRepository userRepository, final ReactiveMongoTemplate mongoTemplate,
                           final DistanceExternalService distanceExternalService) {
        this.maxDistance = new Distance(maxDistance, Metrics.KILOMETERS);
        this.userMapper = userMapper;
        this.geoPointMapper = geoPointMapper;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.distanceExternalService = distanceExternalService;
    }

    @Override
    public Mono<UserDto> addUser(final UserDto userDto) {
        return Mono.just(userDto)
                .map(userMapper::toEntity)
                .flatMap(userRepository::save)
                .map(userMapper::toDto)
                .doOnNext(u -> log.info("User created: {}", u));
    }

    @Override
    public Mono<UserDto> findClosestAvailableProvider(final GeoPointDto geoPointDto) {
        return this.findClosestAvailableProviders(geoPointDto, MAXIMUM_QUERY_RESULTS)
                .collectList()
                .publishOn(Schedulers.elastic())
                .map(users -> distanceExternalService.getClosestUser(users, geoPointDto))
                .timeout(Duration.ofMillis(3000))
                .onErrorResume(e -> findClosestAvailableProviders(geoPointDto, 1)
                        .next()
                        .switchIfEmpty(Mono.error(new ResourceNotFoundExcetion(CANNOT_FIND_AVAILABLE_PROVIDERS,
                                String.format("Could not find any provider for the location with latitude %s " +
                                        "and longitude %s", geoPointDto.getY(), geoPointDto.getX()))
                        )));
    }

    private Flux<UserDto> findClosestAvailableProviders(final GeoPointDto pointDto, final int queryResults) {
        return Mono.just(pointDto)
                .map(point -> this.createNearQuery(point, queryResults))
                .flatMapMany(query -> mongoTemplate.geoNear(query, User.class))
                .map(userMapper::toDto)
                .switchIfEmpty(Mono.error(new ResourceNotFoundExcetion(CANNOT_FIND_AVAILABLE_PROVIDERS,
                        String.format("Could not find any provider for the location with latitude %s " +
                                "and longitude %s", pointDto.getY(), pointDto.getX()))
                ));
    }

    private NearQuery createNearQuery(final GeoPointDto pointDto, final int queryResults) {
        final Query query = new Query(Criteria.where("role").is(PROVIDER).and("available").is(true));
        query.fields().include("firstName").include("lastName").include("location");

        final Point point = geoPointMapper.toPoint(pointDto);
        final NearQuery nearQuery = NearQuery.near(point).maxDistance(maxDistance);
        nearQuery.query(query);
        nearQuery.num(queryResults);
        return nearQuery;
    }

}
