package com.ubb.locexchange.service;

import com.ubb.locexchange.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDto> addUser(final UserDto userDto);

}
