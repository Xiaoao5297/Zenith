package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;

final class IEntitySelector$3 implements IEntitySelector {

    IEntitySelector$3() {}

    @Override
    public boolean func_82704_a(Entity entity) {
        return entity instanceof IInventory && entity.func_70089_S();
    }
}
