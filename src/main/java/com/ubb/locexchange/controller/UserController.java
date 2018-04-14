package com.ubb.locexchange.controller;

import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.UserDto;
import com.ubb.locexchange.service.UserService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/closest")
    public Flux<UserDto> findClosestUsers(@RequestBody final Mono<GeoPointDto> point) {
        return userService.findNearestAvailableProviders(point);
    }

    @PostMapping
    public Mono<UserDto> createUser(@RequestBody @Valid final Mono<UserDto> userDto) {
        return userService.addUser(userDto);
    }

}
