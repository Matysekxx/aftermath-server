package cz.matysekxx.aftermathserver.core.model.entity;

import cz.matysekxx.aftermathserver.core.model.item.Item;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NpcTemplate {
    private String id;
    private String name;
    private String type;
    private String behavior;
    private boolean aggressive;
    private int damage;
    private int range;
    private int maxHp;
    private List<Item> loot = new ArrayList<>();
}
