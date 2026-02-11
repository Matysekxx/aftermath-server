package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing another player visible on the map.
 *
 * @author Matysekxx
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtherPlayerDto {
    private String id;
    private String name;
    private int x;
    private int y;
    private int z;

    /**
     * Creates an OtherPlayerDto from a Player entity.
     *
     * @param player The player entity.
     * @return The DTO.
     */
    public static OtherPlayerDto fromPlayer(Player player) {
        return new OtherPlayerDto(
                player.getId(), player.getName(), player.getX(), player.getY(), player.getLayerIndex()
        );
    }
}
