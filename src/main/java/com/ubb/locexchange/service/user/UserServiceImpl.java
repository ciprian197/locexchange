package com.ubb.locexchange.service.user;

import com.ubb.locexchange.domain.User;
import com.ubb.locexchange.domain.UserStatus;
import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.UpdateUserDto;
import com.ubb.locexchange.dto.UserDto;
import com.ubb.locexchange.exception.ResourceNotFoundExcetion;
import com.ubb.locexchange.mapper.GeoPointMapper;
import com.ubb.locexchange.mapper.UserMapper;
import com.ubb.locexchange.repository.UserRepository;
import com.ubb.locexchange.service.distance.DistanceExternalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
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

import static com.ubb.locexchange.domain.Role.PROVIDER;
import static com.ubb.locexchange.domain.User.Property.*;
import static com.ubb.locexchange.domain.UserStatus.CONNECTED;
import static com.ubb.locexchange.domain.UserStatus.IN_MISSION;
import static com.ubb.locexchange.exception.error.GeneralErrorType.CAN_NOT_FIND_AVAILABLE_PROVIDERS;
import static com.ubb.locexchange.exception.error.GeneralErrorType.USER_NOT_FOUND;

@Slf4j
@Service
class UserServiceImpl implements UserInternalService {

    private static final int MAXIMUM_QUERY_RESULTS = 5;
    private static final int MAXIMUM_COMPUTATION_DURATION_USING_EXTERNAL_SERVICE = 3000;

    private final Distance maxDistance;
    private final UserMapper userMapper;
    private final GeoPointMapper geoPointMapper;
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final ReactiveMongoTemplate mongoTemplate;
    private final DistanceExternalService distanceExternalService;

    public UserServiceImpl(@Value("${distance.maximum.allowed}") final double maxDistance,
                           final UserMapper userMapper, final GeoPointMapper geoPointMapper,
                           final UserRepository userRepository, final UserValidator userValidator,
                           final ReactiveMongoTemplate mongoTemplate,
                           final DistanceExternalService distanceExternalService) {
        this.maxDistance = new Distance(maxDistance, Metrics.KILOMETERS);
        this.userMapper = userMapper;
        this.geoPointMapper = geoPointMapper;
        this.userRepository = userRepository;
        this.userValidator = userValidator;
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
    public Mono<UserDto> updateUserBySessionId(final String webSessionId, final UpdateUserDto updateUserDto) {
        return getUserBySessionId(webSessionId)
                .map(user -> updateUser(user, updateUserDto))
                .flatMap(userRepository::save)
                .map(userMapper::toDto);
    }

    @Override
    public Mono<UserDto> updateUserLocation(final String userId, final GeoPointDto location) {
        return getUserById(userId)
                .map(user -> updateUser(user, location))
                .flatMap(userRepository::save)
                .map(userMapper::toDto);
    }

    @Override
    public Mono<User> getClosestAvailableProvider(final GeoPointDto geoPointDto) {
        return this.findClosestAvailableProviders(geoPointDto, MAXIMUM_QUERY_RESULTS)
                .collectList()
                .publishOn(Schedulers.elastic())
                .map(users -> distanceExternalService.getClosestUser(users, geoPointDto))
                .timeout(Duration.ofMillis(MAXIMUM_COMPUTATION_DURATION_USING_EXTERNAL_SERVICE))
                .doOnError(e -> log.warn("Computing closest provider using Third Party Service has failed, will " +
                        "proceed with fallback computation"))
                .onErrorResume(e -> findClosestAvailableProviderInternal(geoPointDto))
                .flatMap(this::markProviderAsInMission);
    }

    @Override
    public Mono<User> getUser(final String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundExcetion(USER_NOT_FOUND, String.format("User(id=%s) not found", id))));
    }

    private Mono<User> findClosestAvailableProviderInternal(final GeoPointDto geoPointDto) {
        return findClosestAvailableProviders(geoPointDto, 1)
                .next();
    }

    private Flux<User> findClosestAvailableProviders(final GeoPointDto pointDto, final int queryResults) {
        return Mono.just(pointDto)
                .map(point -> this.createNearQuery(point, queryResults))
                .flatMapMany(query -> mongoTemplate.geoNear(query, User.class))
                .map(GeoResult::getContent)
                .switchIfEmpty(Mono.error(new ResourceNotFoundExcetion(CAN_NOT_FIND_AVAILABLE_PROVIDERS,
                        String.format("Could not find any provider for the location with latitude %s " +
                                "and longitude %s", pointDto.getY(), pointDto.getX()))
                ));
    }

    private NearQuery createNearQuery(final GeoPointDto pointDto, final int queryResults) {
        final Query query = new Query(Criteria.where(ROLE.getValue()).is(PROVIDER).and(STATUS.getValue()).is(CONNECTED));
        query.fields().include(FIRST_NAME.getValue()).include(LAST_NAME.getValue()).include(LOCATION.getValue());

        final Point point = geoPointMapper.toPoint(pointDto);
        final NearQuery nearQuery = NearQuery.near(point).maxDistance(maxDistance);

        nearQuery.query(query);
        nearQuery.limit(queryResults);
        return nearQuery;
    }

    private Mono<User> getUserBySessionId(final String webSessionId) {
        return this.userRepository.findByWebSessionId(webSessionId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundExcetion(USER_NOT_FOUND,
                        String.format("User(webSessionId=%s) not found", webSessionId))));
    }

    private Mono<? extends User> markProviderAsInMission(final User user) {
        final UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .userStatus(IN_MISSION).build();
        return userRepository.save(updateUser(user, updateUserDto));
    }

    private Mono<User> getUserById(final String id) {
        return this.userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundExcetion(USER_NOT_FOUND,
                        String.format("Could not find user with id %s ", id))));
    }

    //TODO Will be updated after client user side logic is updated
    private User updateUser(final User user, final UpdateUserDto updateUserDto) {
        final String webSessionId = updateUserDto.getWebSessionId();
        if (webSessionId != null) {
            user.setWebSessionId(webSessionId);
            user.setStatus(UserStatus.CONNECTED);
        }

        final GeoPointDto location = updateUserDto.getLocation();
        if (location != null) {
            userValidator.validateUserForLocationUpdate(user);
            user.setLocation(geoPointMapper.toEntity(location));
        }

        final UserStatus userStatus = updateUserDto.getUserStatus();
        if (userStatus != null) {
            user.setStatus(userStatus);
        }
        return user;
    }

    private User updateUser(final User user, final GeoPointDto location) {
        if (location != null) {
            userValidator.validateUserForLocationUpdate(user);
            user.setLocation(geoPointMapper.toEntity(location));
            user.setStatus(CONNECTED);
        }
        return user;
    }

}
