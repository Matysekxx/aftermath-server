package cz.matysekxx.aftermathserver.config;

import cz.matysekxx.aftermathserver.handler.GameHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final GameHandler gameHandler;

    public WebSocketConfiguration(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameHandler, "/game")
                .setAllowedOrigins("*");
    }
}
