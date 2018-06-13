package com.ubb.locexchange.websockets;


import com.ubb.locexchange.domain.Role;
import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.LocationExchangeDto;
import com.ubb.locexchange.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.ubb.locexchange.util.JsonUtils.convertObjectToJson;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
public class ReactiveWebSocketClient {

    private static final GeoPointDto geoPoint = GeoPointDto.builder()
            .x(26.854890)
            .y(47.43480).build();
    private static final LocationExchangeDto locationExchangeDto = LocationExchangeDto.builder()
            .role(Role.CLIENT)
            .location(geoPoint)
            .build();


    public static void main(final String[] args) {
        final WebSocketClient client = new ReactorNettyWebSocketClient();

        final WebClient webClient = WebClient.create("http://localhost:8080/api/v1/users");

        webClient.post().uri("/provider/closest")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromObject(geoPoint))
                .retrieve()
                .bodyToMono(UserDto.class)
                .doOnNext(n -> log.info("Client received {} ", n))
                .doOnError(e -> log.error("Error while retrieving closest user for coordinates {}", geoPoint, e))
                .flatMap(user -> handleWebSocketConnection(client, user))
                .subscribe();

        for (int i = 0; true; i++) {
            i++;
        }

    }

    private static Mono<Void> handleWebSocketConnection(final WebSocketClient client, final UserDto user) {
        return client.execute(
                URI.create("ws://localhost:8080/client"),
                session -> {
                    locationExchangeDto.setConsigneeId(user.getWebSessionId());
                    return session.send(
                            Flux.range(1, 10000)
                                    .map(i -> session.textMessage(convertObjectToJson(locationExchangeDto))))
                            .thenMany(session.receive()
                                    .map(WebSocketMessage::getPayloadAsText)
                                    .log())
                            .then();
                });
    }

}
