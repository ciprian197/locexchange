package com.ubb.locexchange.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubb.locexchange.dto.LocationExchangeDto;
import com.ubb.locexchange.dto.UpdateUserDto;
import com.ubb.locexchange.dto.UserDto;
import com.ubb.locexchange.service.UserService;
import com.ubb.locexchange.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
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
    public Mono<Void> handle(final WebSocketSession session) {
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(payload -> JsonUtils.getObjectFromString(payload, LocationExchangeDto.class))
                .doOnNext(dto -> onConnect(session))
                .flatMap(dto -> updateUser(session, dto))
                .doFinally(signalType -> onProviderDisconnect(session))
                .then();
    }

    private void onConnect(final WebSocketSession session) {
        if(webSocketSessions.putIfAbsent(session.getId(), session)== null){
            log.info("New session connected with id: " + session.getId());
        }
    }

    private Mono<UserDto> updateUser(final WebSocketSession session, final LocationExchangeDto dto) {
        final UpdateUserDto updateUserDto = createUpdateUserDto(dto, session.getId());
        return userService.updateUser(dto.getUsername(), updateUserDto)
                .doOnNext(user -> log.info(user.toString()));
    }

    private void onProviderDisconnect(final WebSocketSession session) {
        session.close();
        webSocketSessions.remove(session.getId());
        userService.removeWebSessionId(session.getId())
                .doOnNext(u -> log.info("Closing web socket session with id " + session.getId()))
                .subscribe();
    }

    private UpdateUserDto createUpdateUserDto(final LocationExchangeDto locationExchangeDto, final String webSessionId) {
        return UpdateUserDto.builder()
                .location(locationExchangeDto.getLocation())
                .webSessionId(webSessionId).build();

    }

}