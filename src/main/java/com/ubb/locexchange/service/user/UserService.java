package com.ubb.locexchange.service.user;

import com.ubb.locexchange.dto.UpdateUserDto;
import com.ubb.locexchange.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDto> addUser(UserDto userDto);

    Mono<UserDto> updateUserBySessionId(String webSessionId, UpdateUserDto updateUserDto);

}
