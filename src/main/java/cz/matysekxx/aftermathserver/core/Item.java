package cz.matysekxx.aftermathserver.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Item {
    private String id;
    private String name;
    private int quantity;
    private int maxStack;
}
