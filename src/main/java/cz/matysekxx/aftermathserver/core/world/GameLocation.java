package cz.matysekxx.aftermathserver.core.world;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameLocation {
    private String mapId;
    private int layerIndex;
    private Point position;
}