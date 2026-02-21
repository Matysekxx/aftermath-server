package cz.matysekxx.aftermathserver.core.model.entity;

import cz.matysekxx.aftermathserver.core.model.behavior.Behavior;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a Non-Player Character (NPC) in the game world.
 * <p>
 * NPCs can fulfill various roles such as friendly traders, quest givers, or hostile mutants.
 * Their logic and movement are governed by an assigned {@link Behavior} strategy.
 *
 * @author Matysekxx
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class Npc extends Entity {
    /**
     * The AI behavior strategy assigned to this NPC.
     */
    private Behavior behavior;

    /**
     * The type of the NPC (e.g., "MUTANT", "TRADER", "SOLDIER").
     */
    private String type;

    /**
     * Indicates if the NPC is hostile towards players.
     */
    private boolean aggressive;

    /**
     * Base damage dealt by this NPC in combat.
     */
    private int damage;

    /**
     * Range of vision or attack.
     */
    private int range;

    /**
     * Items dropped when this NPC dies.
     */
    private Collection<Item> loot = new ArrayList<>();
    /**
     * Items available for purchase from the npc.
     */
    private List<Item> shopItems = new ArrayList<>();

    /**
     * ID of the dialogue tree associated with this NPC (if interactive).
     */
    private String dialogueId;
    /**
     * The type of interaction this NPC provides.
     */
    private InteractionType interaction;

    /**
     * Constructs a new Npc instance.
     *
     * @param id          The NPC ID.
     * @param name        The NPC name.
     * @param x           The X coordinate.
     * @param y           The Y coordinate.
     * @param layerIndex  The layer index.
     * @param mapId       The map ID.
     * @param maxHp       The maximum health points.
     * @param behavior    The AI behavior.
     * @param interaction The interaction type.
     */
    public Npc(String id, String name, int x, int y, int layerIndex, String mapId, int maxHp, Behavior behavior, InteractionType interaction) {
        super(x, y, layerIndex, mapId, id, name, maxHp, maxHp, State.ALIVE);
        this.behavior = behavior;
        this.interaction = interaction;
    }

    /**
     * Creates an NPC instance from a template.
     *
     * @param instanceId The unique instance ID.
     * @param template   The NPC template.
     * @param x          The X coordinate.
     * @param y          The Y coordinate.
     * @param layerIndex The layer index.
     * @param mapId      The map ID.
     * @param behavior   The AI behavior.
     * @return A new Npc instance.
     */
    public static Npc fromTemplate(String instanceId, NpcTemplate template, int x, int y, int layerIndex, String mapId, Behavior behavior) {
        final Npc npc = new Npc(instanceId, template.getName(), x, y, layerIndex, mapId, template.getMaxHp(), behavior, template.getInteraction());
        npc.setType(template.getType());
        npc.setAggressive(template.isAggressive());
        npc.setDamage(template.getDamage());
        npc.setRange(template.getRange());
        npc.setDialogueId(template.getDialogId());
        npc.setShopItems(new ArrayList<>(template.getShopItems()));
        return npc;
    }

    /**
     * Updates the NPC state by executing its behavior logic.
     *
     * @param gameMapData The map data.
     * @param players     The collection of players on the map.
     */
    public void update(GameMapData gameMapData, Collection<Player> players) {
        behavior.update(this, gameMapData, players);
    }
}
