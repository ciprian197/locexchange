package com.ubb.locexchange.rsocket;

import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.UserDto;
import com.ubb.locexchange.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ProviderSocketChannel {

    private static final ConcurrentMap<String, RSocketRequester> USER_CONNECTIONS = new ConcurrentHashMap<>();

    private final UserService userService;

    @MessageMapping("provider.{userId}")
    public Flux<UserDto> providerChannel(final Flux<GeoPointDto> locations, @DestinationVariable("userId") final String userId,
                                         final RSocketRequester rSocketRequester) {
        return locations
                .flatMap(location -> this.updateUser(userId, location));
    }

    @ConnectMapping("provider.{userId}")
    public void addRSocketRequesterToUser(@DestinationVariable("userId") final String userId, final RSocketRequester rSocketRequester) {
        rSocketRequester.rsocket()
                .onClose()
                .doFirst(() -> {
                    log.info("Client: {} CONNECTED.", userId);
                    USER_CONNECTIONS.putIfAbsent(userId, rSocketRequester);
                })
                .doOnError(error -> {
                    log.warn("Channel to client {} CLOSED", userId);
                })
                .doFinally(consumer -> {
                    USER_CONNECTIONS.remove(rSocketRequester);
                    log.info("Client {} DISCONNECTED", userId);
                })
                .subscribe();
    }

    private Mono<UserDto> updateUser(final String userId, final GeoPointDto location) {
        return userService.updateUserLocation(userId, location)
                .doOnNext(user -> log.info(user.toString()));
    }

}
