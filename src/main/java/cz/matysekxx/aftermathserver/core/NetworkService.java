package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.dto.GameDtos;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.TreeMap;

@Service
public class NetworkService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendGameOver(Player p) {
        final TreeMap<String, Object> gameOverData = new TreeMap<>();
        gameOverData.put("message", "YOU DIED");
        gameOverData.put("respawn_possible", true);
        try {
            final String json = objectMapper.writeValueAsString(WebSocketResponse.of("GAME_OVER", gameOverData));
            p.getSession().sendMessage(new TextMessage(json));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void sendStatsToClient(Player p) {
        if (p.getSession().isOpen()) {
            try {
                final GameDtos.StatsResponse stats = GameDtos.StatsResponse.of(p);
                final String json = objectMapper.writeValueAsString(WebSocketResponse.of("STATS_UPDATE", stats));
                p.getSession().sendMessage(new TextMessage(json));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
