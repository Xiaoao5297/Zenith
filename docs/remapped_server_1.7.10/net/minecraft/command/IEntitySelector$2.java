package net.minecraft.command;

import net.minecraft.entity.Entity;

final class IEntitySelector$2 implements IEntitySelector {

    IEntitySelector$2() {}

    @Override
    public boolean func_82704_a(Entity entity) {
        return entity.func_70089_S() && entity.field_70153_n == null && entity.field_70154_o == null;
    }
}
