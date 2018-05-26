package com.ubb.locexchange.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubb.locexchange.dto.LocationExchangeDto;
import com.ubb.locexchange.dto.UpdateUserDto;
import com.ubb.locexchange.dto.UserDto;
import com.ubb.locexchange.service.UserService;
import com.ubb.locexchange.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@Scope(value = "prototype")
public class ProviderConnectionWebSocketHandler implements WebSocketHandler {

    private static final ObjectMapper json = new ObjectMapper();

    private final UserService userService;
    private final Map<String, WebSocketSession> webSocketSessions;

    public ProviderConnectionWebSocketHandler(final UserService userService,
                                              final Map<String, WebSocketSession> webSocketSessions) {
        this.userService = userService;
        this.webSocketSessions = webSocketSessions;
    }

    @Override
    public Mono<Void> handle(final WebSocketSession webSocketSession) {
        return webSocketSession.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(payload -> JsonUtils.getObjectFromString(payload, LocationExchangeDto.class))
                .doOnNext(dto -> webSocketSessions.putIfAbsent(webSocketSession.getId(), webSocketSession))
                .flatMap(dto -> updateUser(webSocketSession, dto))
                .doFinally(signalType -> closeWebSocketSession(webSocketSession))
                .then();
    }

    private Mono<UserDto> updateUser(final WebSocketSession webSocketSession, final LocationExchangeDto dto) {
        final UpdateUserDto updateUserDto = createUpdateUserDto(dto, webSocketSession.getId());
        return userService.updateUser(dto.getUsername(), updateUserDto)
                .doOnNext(user -> log.info(user.toString()));
    }

    private void closeWebSocketSession(final WebSocketSession webSocketSession) {
        webSocketSession.close();
        webSocketSessions.remove(webSocketSession.getId());
        userService.removeWebSessionId(webSocketSession.getId())
                .doOnNext(u -> log.info("Closing web socket session with id " + webSocketSession.getId()))
                .subscribe();
    }

    private UpdateUserDto createUpdateUserDto(final LocationExchangeDto locationExchangeDto, final String webSessionId) {
        return UpdateUserDto.builder()
                .location(locationExchangeDto.getLocation())
                .webSessionId(webSessionId).build();

    }

}