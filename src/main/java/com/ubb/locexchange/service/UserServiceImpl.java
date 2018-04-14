package com.ubb.locexchange.service;

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

    public UserServiceImpl(@Value("${distance.maximum.allowed}") final double maxDistance,
                           final UserMapper userMapper, final GeoPointMapper geoPointMapper,
                           final UserRepository userRepository, final ReactiveMongoTemplate mongoTemplate) {
        this.maxDistance = new Distance(maxDistance, Metrics.KILOMETERS);
        this.userMapper = userMapper;
        this.geoPointMapper = geoPointMapper;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<UserDto> addUser(final Mono<UserDto> userDto) {
        return userDto.map(userMapper::toEntity)
                .flatMap(userRepository::save)
                .map(userMapper::toDto)
                .doOnNext(u -> log.info("User created: {}", u));
    }

    @Override
    public Flux<UserDto> findNearestAvailableProviders(final Mono<GeoPointDto> pointDto) {
        return pointDto.map(this::createNearQuery)
                .flatMapMany(query -> mongoTemplate.geoNear(query, User.class))
                .map(userMapper::toDto);
    }

    private NearQuery createNearQuery(final GeoPointDto pointDto) {
        final Query query = new Query(Criteria.where("role").is(PROVIDER).and("available").is(true));
        query.fields().include("firstName").include("lastName").include("location");

        final Point point = geoPointMapper.toPoint(pointDto);
        final NearQuery nearQuery = NearQuery.near(point).maxDistance(maxDistance);
        nearQuery.query(query);
        nearQuery.num(MAXIMUM_QUERY_RESULTS);
        return nearQuery;
    }

}
