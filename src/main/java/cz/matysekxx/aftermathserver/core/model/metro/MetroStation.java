package cz.matysekxx.aftermathserver.core.model.metro;

import cz.matysekxx.aftermathserver.core.model.Player;
import lombok.Data;

@Data
public class MetroStation {
    private String id;
    private String name;

    public boolean canPlayerTravel(Player player) {
        //TODO: pridat logiku rozhodovani  zda hrac muze cestovat na stanici
        return false;
    }
}
