package cz.matysekxx.aftermathserver.core.world;

import lombok.Data;

@Data
public class MapObject {
    private String type;
    private int x, y;
    private String id;
    private String action;
}
