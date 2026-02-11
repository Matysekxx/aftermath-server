package cz.matysekxx.aftermathserver.config;

import cz.matysekxx.aftermathserver.websocket.GameHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configures WebSocket handling for the application.
 * <p>
 * Enables WebSocket support and registers the game handler to a specific endpoint.
 *
 * @author Matysekxx
 */
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final GameHandler gameHandler;

    public WebSocketConfiguration(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    /**
     * Registers the WebSocket handler to the {@code /game} endpoint.
     *
     * @param registry The registry used to configure WebSocket handlers.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameHandler, "/game")
                .setAllowedOrigins("*");
    }
}