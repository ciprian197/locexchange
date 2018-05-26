package com.ubb.locexchange.websockets;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.LocationExchangeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
public class ReactiveWebSocketProvider {

    private static final ObjectMapper json = new ObjectMapper();
    private static final LocationExchangeDto locationExchangeDto = LocationExchangeDto.builder()
            .username("ciprianm")
            .location(GeoPointDto.builder()
                    .x(26.854890)
                    .y(47.434550).build())
            .build();


    public static void main(final String[] args) {

        final WebSocketClient client = new ReactorNettyWebSocketClient();
        client.execute(
                URI.create("ws://localhost:8080/provider"),
                session -> {
                    try {
                        return session.send(
                                Mono.just(session.textMessage(json.writeValueAsString(locationExchangeDto))))
                                .and(session.receive()
                                        .doOnNext(message -> log.info(message.getPayloadAsText())));
                    } catch (final JsonProcessingException e) {
                        e.printStackTrace();
                        return Mono.empty();
                    }
                })
                .subscribe();
        for (int i = 0; true; i++) {
            i++;
        }
    }


}
