package com.ubb.locexchange.service;

import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.UserDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDto> addUser(UserDto userDto);

    Flux<UserDto> findNearestAvailableProviders(Mono<GeoPointDto> point);

}
