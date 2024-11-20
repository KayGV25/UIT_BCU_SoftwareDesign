package vn.edu.uit.csbu.software_design.software_design_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
// import org.springframework.lang.NonNull;
// import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
// import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
// import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import vn.edu.uit.csbu.software_design.software_design_backend.chat.ChatHandler;

@Configuration
// @
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    // @Override
    // public void configureMessageBroker(@NonNull MessageBrokerRegistry config){
    //     config.enableSimpleBroker("/topic"); // Messages will be sent to "/topic"
    //     config.setApplicationDestinationPrefixes("/app"); // Client messages to "/app"
    // }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatHandler(), "/chat").setAllowedOrigins("*");
    }
}
