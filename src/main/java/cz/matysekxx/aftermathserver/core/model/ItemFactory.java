package cz.matysekxx.aftermathserver.core.model;

import org.springframework.stereotype.Service;

@Service
public class ItemFactory {

    public Item createMedkit() {
        return Item.builder()
                .id("medkit_basic")
                .name("Basic Medkit")
                .description("A small first aid kit.")
                .symbol("+")
                .type(ItemType.CONSUMABLE)
                .healAmount(30)
                .quantity(1)
                .maxStack(5)
                .weight(0.5)
                .price(50)
                .build();
    }

    public Item createPistol() {
        return Item.builder()
                .id("pistol_9mm")
                .name("9mm Pistol")
                .description("Reliable sidearm.")
                .symbol("p")
                .type(ItemType.WEAPON)
                .damage(15)
                .quantity(1)
                .maxStack(1)
                .weight(1.2)
                .price(200)
                .build();
    }

    public Item createScrapMetal() {
        return Item.builder()
                .id("scrap_metal")
                .name("Scrap Metal")
                .description("Rusty pieces of metal.")
                .symbol("x")
                .type(ItemType.RESOURCE)
                .quantity(1)
                .maxStack(20)
                .weight(0.2)
                .price(10)
                .build();
    }
}
