package net.minecraft.entity.passive;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

class EntitySheep$1 extends Container {

    EntitySheep$1(EntitySheep entitysheep) {
        this.field_90034_a = entitysheep;
    }

    @Override
    public boolean func_75145_c(EntityPlayer entityplayer) {
        return false;
    }
}
