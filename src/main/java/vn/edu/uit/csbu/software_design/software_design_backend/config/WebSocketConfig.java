package vn.edu.uit.csbu.software_design.software_design_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import vn.edu.uit.csbu.software_design.software_design_backend.chat.ChatHandler;

/**
 * The WebSocketConfig class configures WebSocket support in a Java application with a ChatHandler for
 * handling chat-related interactions.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    // This method `registerWebSocketHandlers` is used to register a WebSocket handler for a specific
    // endpoint `/chat` in the application. It creates a new instance of `ChatHandler` to handle
    // WebSocket interactions related to chat functionality. The `setAllowedOrigins("*")` method is
    // used to allow connections from any origin.
    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatHandler(), "/chat").setAllowedOrigins("*");
    }

    /**
     * Instantiates a new Web socket config.
     */
// The `public WebSocketConfig(){}` is a default constructor for the `WebSocketConfig` class. In
    // this case, it is an empty constructor that does not contain any specific logic or initialization
    // code. It is often used when no additional initialization is needed for an object of this class.
    public WebSocketConfig(){
    }
}
