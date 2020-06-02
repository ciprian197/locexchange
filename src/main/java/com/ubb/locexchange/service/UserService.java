package com.ubb.locexchange.service;

import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.UpdateUserDto;
import com.ubb.locexchange.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDto> addUser(UserDto userDto);

    Mono<UserDto> updateUserBySessionId(String webSessionId, UpdateUserDto updateUserDto);

    Mono<UserDto> updateUserLocation(String userId, GeoPointDto location);

    Mono<UserDto> findClosestAvailableProvider(GeoPointDto geoPointDto);

    Mono<UserDto> removeWebSessionId(String webSessionId);
}
