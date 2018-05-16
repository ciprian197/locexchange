package com.ubb.locexchange.service;

import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDto> addUser(UserDto userDto);

    Mono<UserDto> updateUserLocation(String id, GeoPointDto pointDto);

    Mono<UserDto> findClosestAvailableProvider(GeoPointDto geoPointDto);

}
