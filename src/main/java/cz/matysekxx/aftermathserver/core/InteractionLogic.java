package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.Item;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.dto.GameDtos;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;

import java.util.ArrayList;
import java.util.List;

public abstract class InteractionLogic {
    public abstract WebSocketResponse interact(MapObject target, Player player);

    public static class ReadLogic extends InteractionLogic {
        @Override
        public WebSocketResponse interact(MapObject target, Player player) {
            return WebSocketResponse.of("NOTIFICATION", target.getDescription());
        }
    }

    public static class LootLogic extends InteractionLogic {
        @Override
        public WebSocketResponse interact(MapObject target, Player player) {
            synchronized (target) {
                if (target.getItems().isEmpty())
                    return WebSocketResponse.of("NOTIFICATION", target.getDescription() + "It is empty");

                final StringBuilder message = new StringBuilder(target.getDescription() + "\nYou found:");
                final List<Item> itemsToRemove = new ArrayList<>();

                for (Item item : target.getItems()) {
                    if (player.getInventory().addItem(item)) {
                        message.append("\n + ").append(item.getQuantity()).append("x ").append(item.getName());
                        itemsToRemove.add(item);
                    } else {
                        message.append("\n ! ").append(item.getName()).append("It is too heavy for you");
                    }
                }

                target.getItems().removeAll(itemsToRemove);

                if (target.getItems().isEmpty()) {
                    target.setDescription("Empty");
                }
                return WebSocketResponse.of("LOOT_SUCCESS", message.toString());
            }
        }
    }

    public static class TravelLogic extends InteractionLogic {
        private final WorldManager worldManager;

        public TravelLogic(WorldManager worldManager) {
            this.worldManager = worldManager;
        }

        @Override
        public WebSocketResponse interact(MapObject target, Player player) {
            final String nextMapId = target.getTargetMapId();
            final GameMapData nextMap = worldManager.getMap(nextMapId);

            if (nextMap == null) {
                return WebSocketResponse.of("ACTION_FAILED", "Target map does not exist (server error).");
            }

            player.setCurrentMapId(nextMapId);
            player.setX(target.getTargetX());
            player.setY(target.getTargetY());

            final GameDtos.MapLoadPayload mapPayload = new GameDtos.MapLoadPayload(
                    nextMap.getId(),
                    nextMap.getName(),
                    nextMap.getLayout(),
                    nextMap.getEnvironment(),
                    nextMap.getParsedLayers()
            );
            return WebSocketResponse.of("MAP_LOAD", mapPayload);
        }
    }
}
