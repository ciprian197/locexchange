package com.ubb.locexchange.websockets;

import com.ubb.locexchange.domain.UserStatus;
import com.ubb.locexchange.dto.LocationExchangeDto;
import com.ubb.locexchange.dto.UpdateUserDto;
import com.ubb.locexchange.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.ubb.locexchange.util.JsonUtils.convertObjectToJson;
import static com.ubb.locexchange.util.JsonUtils.getObjectFromString;

@Slf4j
@Component
@Scope(value = "prototype")
public class ClientConnectionWebSocketHandler implements WebSocketHandler {

    private final UserService userService;
    private final Map<String, WebSocketSession> webSocketSessions;

    public ClientConnectionWebSocketHandler(final UserService userService,
                                            final Map<String, WebSocketSession> webSocketSessions) {
        this.userService = userService;
        this.webSocketSessions = webSocketSessions;
    }

    @Override
    public Mono<Void> handle(final WebSocketSession webSocketSession) {
        return webSocketSession.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(payload -> getObjectFromString(payload, LocationExchangeDto.class))
                .doOnNext(exchangeDto -> checkIfSessionIsSaved(webSocketSession, exchangeDto.getUsername()))
                .map(dto -> {
                    final WebSocketSession session = webSocketSessions.get(dto.getUsername());
                    return session.send(Mono.just(session.textMessage(convertObjectToJson(dto.getLocation()))))
                            .subscribe(a -> log.info("Message send"));
                })
                .then();
    }

    private void checkIfSessionIsSaved(final WebSocketSession session, final String providerSessionId) {
        if (webSocketSessions.putIfAbsent(session.getId(), session) == null) {
            log.info("New session connected with id: " + session.getId());
            final UpdateUserDto updateUserDto = UpdateUserDto.builder()
                    .userStatus(UserStatus.IN_MISSION).build();
            userService.updateUserBySessionId(providerSessionId, updateUserDto)
                    .subscribe();
        }
    }

}
