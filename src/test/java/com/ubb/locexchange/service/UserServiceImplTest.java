package com.ubb.locexchange.service;

import com.ubb.locexchange.domain.User;
import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.UserDto;
import com.ubb.locexchange.factory.GeoPointFactory;
import com.ubb.locexchange.factory.UserFactory;
import com.ubb.locexchange.mapper.GeoPointMapper;
import com.ubb.locexchange.mapper.UserMapper;
import com.ubb.locexchange.repository.UserRepository;
import com.ubb.locexchange.service.validator.UserValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.NearQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private GeoPointMapper geoPointMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserValidator userValidator;
    @Mock
    private ReactiveMongoTemplate mongoTemplate;
    @Mock
    private DistanceExternalService distanceExternalService;

    private UserServiceImpl userService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserServiceImpl(2.3, userMapper, geoPointMapper, userRepository, userValidator,
                mongoTemplate, distanceExternalService);
    }

    @Test
    public void findClosestAvailableProvider_externalCallIsOk_closestProvider() {
        // Given
        final Flux<GeoResult<User>> geoResult = UserFactory.generateProviders()
                .map(user -> new GeoResult<>(user, mock(Distance.class)));
        final GeoPointDto geoPointDto = GeoPointFactory.generateGeoPointDto();
        final User closestProvider = UserFactory.generateProvider();
        final UserDto expected = mock(UserDto.class);

        when(mongoTemplate.geoNear(any(NearQuery.class), eq(User.class)))
                .thenReturn(geoResult);
        when(distanceExternalService.getClosestUser(any(), eq(geoPointDto)))
                .thenReturn(closestProvider);
        when(geoPointMapper.toPoint(geoPointDto))
                .thenReturn(mock(Point.class));
        when(userRepository.save(closestProvider))
                .thenReturn(Mono.just(closestProvider));
        when(userMapper.toDto(closestProvider))
                .thenReturn(expected);

        // When
        final Mono<UserDto> provider = userService.findClosestAvailableProvider(geoPointDto);

        // Then
        StepVerifier.create(provider)
                .expectNext(expected)
                .verifyComplete();
    }

}
