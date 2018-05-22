package com.ubb.locexchange.service;

import com.ubb.locexchange.domain.UserStatus;
import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.UpdateUserDto;
import com.ubb.locexchange.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDto> addUser(UserDto userDto);

    Mono<UserDto> updateUser(String id,  UpdateUserDto updateUserDto);

    Mono<UserDto> findClosestAvailableProvider(GeoPointDto geoPointDto);

}
