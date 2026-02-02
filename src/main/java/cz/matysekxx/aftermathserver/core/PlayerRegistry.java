package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
public class PlayerRegistry {
    private final Map<String, Player> players = new ConcurrentHashMap<>();

    public Player getPlayer(String id) {
        return players.get(id);
    }

    public void put(Player player) {
        players.put(player.getId(), player);
    }

    public void put(String id, Player player) {
        players.put(id, player);
    }

    public void remove(String id) {
        players.remove(id);
    }

    public Optional<Player> getMaybePlayer(String id) {
        if (players.containsKey(id)) return Optional.of(players.get(id));
        return Optional.empty();
    }

    public void forEach(Consumer<Player> action) {
        for (Player player : players.values()) {
            if (player != null) action.accept(player);
        }
    }

    public void forEachWithPredicate(Predicate<Player> predicate, Consumer<Player> action) {
        for (Player player : players.values()) {
            if (player != null && predicate.test(player)) action.accept(player);
        }
    }

    public boolean containsId(String id) {
        return players.containsKey(id);
    }

}
