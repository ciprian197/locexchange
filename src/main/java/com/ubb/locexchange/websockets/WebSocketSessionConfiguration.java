package com.ubb.locexchange.websockets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class WebSocketSessionConfiguration {

    @Bean
    public ConcurrentMap<String, WebSocketSession> webSocketSessions() {
        return new ConcurrentHashMap<>();
    }

}
