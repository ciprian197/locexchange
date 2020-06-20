package com.ubb.locexchange.controller;

import com.ubb.locexchange.dto.UserDto;
import com.ubb.locexchange.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public Mono<UserDto> createUser(@RequestBody @Valid final Mono<UserDto> userDto) {
        return userDto.flatMap(userService::addUser);
    }

}
