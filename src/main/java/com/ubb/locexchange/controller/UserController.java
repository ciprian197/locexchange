package com.ubb.locexchange.controller;

import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.UserDto;
import com.ubb.locexchange.service.UserService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

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

//    @PostMapping("/update/location/{id}")
//    public Mono<UserDto> updateUserLocation(@PathVariable final String id,
//                                            @RequestBody @Valid final Mono<GeoPointDto> pointDto) {
//        return pointDto.flatMap(point -> {
//            this.userService.updateUserLocation(id, point)
//        });
//    }

    @PostMapping("/closest")
    public Mono<UserDto> findClosestUser(@RequestBody @Valid final Mono<GeoPointDto> point) {
        return point.flatMap(userService::findClosestAvailableProvider);
    }

}
