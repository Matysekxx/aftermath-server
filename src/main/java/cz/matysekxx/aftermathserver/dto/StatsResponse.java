package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO for sending player statistics to the client.
 *
 * @author Matysekxx
 */
@Getter
@AllArgsConstructor
public class StatsResponse {
    /** Current health points. */
    private int hp;
    /** Maximum health points. */
    private int maxHp;
    /** Current radiation level. */
    private int rads;
    /** Current credits balance. */
    private int credits;
    /** Current debt. */
    private int debt;
    /** Current global debt. */
    private long globalDebt;
    private Integer equippedWeaponSlot;
    private Integer equippedMaskSlot;

    /**
     * Creates a StatsResponse from a Player entity.
     *
     * @param player The player entity.
     * @param globalDebt The current global debt.
     * @return A new StatsResponse instance.
     */
    public static StatsResponse of(Player player, long globalDebt) {
        return new StatsResponse(
            player.getHp(), 
            player.getMaxHp(), 
            player.getRads(), 
            player.getCredits(), 
            player.getDebt(),
            globalDebt,
            player.getEquippedWeaponSlot(),
            player.getEquippedMaskSlot()
        );
    }
}
