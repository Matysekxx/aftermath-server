package cz.matysekxx.aftermathserver.core.model.metro;

import cz.matysekxx.aftermathserver.core.model.Player;
import lombok.Data;

/// Represents a single station in the metro network.
@Data
public class MetroStation {
    private String id;
    private String name;

    public MetroStation() {}

    /// Checks if the player meets requirements to travel to this station.
    @Deprecated(forRemoval = true)
    public boolean canPlayerTravel(Player player) {
        //TODO: pridat logiku rozhodovani  zda hrac muze cestovat na stanici
        return false;
    }
}
