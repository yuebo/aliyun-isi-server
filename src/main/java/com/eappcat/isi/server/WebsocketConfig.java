package com.eappcat.isi.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
public class WebsocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(speakWebsocketHandler(),"/ws/v1").setAllowedOrigins("*");
    }
    @Bean
    public SpeechWebsocketHandler speakWebsocketHandler() {
        return new SpeechWebsocketHandler();
    }
}
