package com.ubb.locexchange.websocket;

import com.ubb.locexchange.domain.UserStatus;
import com.ubb.locexchange.dto.LocationExchangeDto;
import com.ubb.locexchange.dto.UpdateUserDto;
import com.ubb.locexchange.exception.ConnectionException;
import com.ubb.locexchange.exception.error.GeneralErrorType;
import com.ubb.locexchange.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentMap;

import static com.ubb.locexchange.util.JsonUtils.convertObjectToJson;
import static com.ubb.locexchange.util.JsonUtils.getObjectFromString;

@Slf4j
@Component
public class ClientConnectionWebSocketHandler implements WebSocketHandler {

    private final UserService userService;
    private final ConcurrentMap<String, WebSocketSession> webSocketSessions;
    private final ConcurrentMap<String, String> assigneesMap;

    public ClientConnectionWebSocketHandler(final UserService userService,
                                            final ConcurrentMap<String, WebSocketSession> webSocketSessions,
                                            final ConcurrentMap<String, String> assigneesMap) {
        this.userService = userService;
        this.webSocketSessions = webSocketSessions;
        this.assigneesMap = assigneesMap;
    }

    @Override
    public Mono<Void> handle(final WebSocketSession session) {
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(payload -> getObjectFromString(payload, LocationExchangeDto.class))
                .doOnNext(exchangeDto -> checkIfSessionIsSaved(session,
                        exchangeDto.getConsigneeId()))
                .map(this::sendMessageToProvider)
                .doFinally(signalType -> onClientDisconnect(session))
                .then();
    }

    private Disposable sendMessageToProvider(final LocationExchangeDto dto) {
        final WebSocketSession session = webSocketSessions.get(dto.getConsigneeId());
        if (session == null) {
            throw new ConnectionException(GeneralErrorType.SESSION_NOT_FOUND,
                    String.format("Can not send message to session %s, the session does not exists",
                            dto.getConsigneeId()));
        }
        return session.send(Mono.just(session.textMessage(convertObjectToJson(dto.getLocation()))))
                .subscribe(a -> log.info("Message send"));
    }

    private void onClientDisconnect(final WebSocketSession session) {
        final UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .userStatus(UserStatus.CONNECTED).build();
        session.close();
        this.userService.updateUserBySessionId(assigneesMap.get(session.getId()), updateUserDto)
                .doOnNext(user -> {
                    assigneesMap.remove(session.getId());
                    webSocketSessions.remove(session.getId());
                    log.info("Closing web socket session with id " + session.getId());
                })
                .subscribe();
    }

    private void checkIfSessionIsSaved(final WebSocketSession session, final String providerSessionId) {
        if (webSocketSessions.putIfAbsent(session.getId(), session) == null) {
            assigneesMap.putIfAbsent(session.getId(), providerSessionId);
            log.info("New session connected with id: " + session.getId());
        }
    }

}
