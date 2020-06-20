package com.ubb.locexchange.service.user;

import com.ubb.locexchange.domain.User;
import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserInternalService extends UserService {

    Mono<User> getClosestAvailableProvider(GeoPointDto geoPointDto);

    Mono<User> getUser(String id);

    Mono<UserDto> updateUserLocation(String userId, GeoPointDto location);

}
