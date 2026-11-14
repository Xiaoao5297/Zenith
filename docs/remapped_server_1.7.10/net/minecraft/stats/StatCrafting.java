package net.minecraft.stats;

import net.minecraft.item.Item;
import net.minecraft.util.IChatComponent;

public class StatCrafting extends StatBase {

    private final Item field_150960_a;

    public StatCrafting(String s, IChatComponent ichatcomponent, Item item) {
        super(s, ichatcomponent);
        this.field_150960_a = item;
    }
}
