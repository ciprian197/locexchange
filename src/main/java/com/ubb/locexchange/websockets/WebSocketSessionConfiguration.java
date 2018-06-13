package com.ubb.locexchange.websockets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.Pair;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class WebSocketSessionConfiguration {

    // store each webSocketSession
    @Bean
    public ConcurrentMap<String, WebSocketSession> webSocketSessions() {
        return new ConcurrentHashMap<>();
    }

    // store each client id with the assigned provider id
    @Bean
    public ConcurrentMap<String, String> assigneesMap() {
        return new ConcurrentHashMap<>();
    }

}
