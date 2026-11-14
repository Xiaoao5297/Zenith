package net.minecraft.command;

import net.minecraft.entity.Entity;

final class IEntitySelector$1 implements IEntitySelector {

    IEntitySelector$1() {}

    @Override
    public boolean func_82704_a(Entity entity) {
        return entity.func_70089_S();
    }
}
