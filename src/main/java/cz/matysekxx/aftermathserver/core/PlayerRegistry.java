package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/// Registry for managing active player sessions.
///
/// Provides thread-safe storage and retrieval of player entities,
/// along with functional iteration methods.
@Service
public class PlayerRegistry {
    private final Map<String, Player> players = new ConcurrentHashMap<>();

    /// Retrieves a player by their session ID.
    public Player getPlayer(String id) {
        return players.get(id);
    }

    /// Registers a player in the registry.
    public void put(Player player) {
        players.put(player.getId(), player);
    }

    /// Registers a player with a specific ID.
    public void put(String id, Player player) {
        players.put(id, player);
    }

    /// Removes a player from the registry.
    public void remove(String id) {
        players.remove(id);
    }

    /// Retrieves a player wrapped in an Optional.
    public Optional<Player> getMaybePlayer(String id) {
        if (players.containsKey(id)) return Optional.of(players.get(id));
        return Optional.empty();
    }

    /// Performs an action for each registered player.
    public void forEach(Consumer<Player> action) {
        for (Player player : players.values()) {
            if (player != null) action.accept(player);
        }
    }

    /// Performs an action for each player that matches the given predicate.
    public void forEachWithPredicate(Predicate<Player> predicate, Consumer<Player> action) {
        for (Player player : players.values()) {
            if (player != null && predicate.test(player)) action.accept(player);
        }
    }

    /// Checks if a player ID exists in the registry.
    public boolean containsId(String id) {
        return players.containsKey(id);
    }

}
