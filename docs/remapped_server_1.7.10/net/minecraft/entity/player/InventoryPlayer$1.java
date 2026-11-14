package net.minecraft.entity.player;

import java.util.concurrent.Callable;
import net.minecraft.item.ItemStack;

class InventoryPlayer$1 implements Callable {

    InventoryPlayer$1(InventoryPlayer inventoryplayer, ItemStack itemstack) {
        this.field_96633_b = inventoryplayer;
        this.field_96634_a = itemstack;
    }

    public String call() {
        return this.field_96634_a.func_82833_r();
    }
}
