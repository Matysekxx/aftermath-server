package cz.matysekxx.aftermathserver.core.world;

import lombok.Data;

import java.awt.*;

@Data
public class Exit {
    private String id;

    private Point sourcePosition;
    private int sourceLayerIndex;

    private String targetMapId;
    
    private int targetLayerIndex;
    private String targetSpawnPoint;
}