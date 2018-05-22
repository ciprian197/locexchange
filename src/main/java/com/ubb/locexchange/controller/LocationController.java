package com.ubb.locexchange.controller;

import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.LocationExchangeDto;
import com.ubb.locexchange.dto.UpdateUserDto;
import com.ubb.locexchange.dto.UserDto;
import com.ubb.locexchange.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static com.ubb.locexchange.domain.UserStatus.CONNECTED;

@Slf4j
@Controller
public class LocationController {

    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public LocationController(final UserService userService, final SimpMessagingTemplate simpMessagingTemplate) {
        this.userService = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    @MessageMapping("/user.private.{username}")
    public void sendMessage(@Payload final LocationExchangeDto locationExchangeDto,
                            @DestinationVariable("username") final String username,
                            final Principal principal) {
        locationExchangeDto.setUsername(principal.getName());
        simpMessagingTemplate.convertAndSend("/user/" + username +
                "/exchange/amq.direct/chat.message", locationExchangeDto);
    }

    @MessageMapping("/locexchange.newClient")
    public void addClient(@Payload final LocationExchangeDto locationExchangeDto,
                          final SimpMessageHeaderAccessor headerAccessor, final Principal principal) {
        log.info("Adding client");
        headerAccessor.getSessionAttributes().put("username", headerAccessor.getDestination());
        userService.findClosestAvailableProvider(locationExchangeDto.getLocation())
                .doOnNext(user -> log.info("Sending" + user))
                .doOnError(e -> log.info("Error occured " + e.getMessage()))
                .subscribe(user -> simpMessagingTemplate.convertAndSend("/user/" + principal.getName()
                        + "/queue/private", user));

    }

    @MessageMapping("/user.addProvider")
    public void addProvider(@Payload final LocationExchangeDto locationExchangeDto,
                            final SimpMessageHeaderAccessor headerAccessor) {
        log.info("Adding provider");
        final UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .userStatus(CONNECTED)
                .location(locationExchangeDto.getLocation()).build();
        headerAccessor.getSessionAttributes().put("username", headerAccessor.getDestination());
    }

    @SubscribeMapping("/client.provider")
    public Mono<UserDto> retrieveProvider(@Payload final GeoPointDto geoPointDto) {
        return userService.findClosestAvailableProvider(geoPointDto);
    }

}
