package com.ubb.locexchange.websockets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReactiveWebSocketConfiguration {

    private final ClientConnectionWebSocketHandler clientConnectionWebSocketHandler;
    private final ProviderConnectionWebSocketHandler providerConnectionWebSocketHandler;

    public ReactiveWebSocketConfiguration(final ClientConnectionWebSocketHandler clientConnectionWebSocketHandler,
                                          final ProviderConnectionWebSocketHandler providerConnectionWebSocketHandler) {
        this.clientConnectionWebSocketHandler = clientConnectionWebSocketHandler;
        this.providerConnectionWebSocketHandler = providerConnectionWebSocketHandler;
    }

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        final Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/client", clientConnectionWebSocketHandler);
        map.put("/provider", providerConnectionWebSocketHandler);

        final SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

}