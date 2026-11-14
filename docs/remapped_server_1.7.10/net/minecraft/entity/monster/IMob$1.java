package net.minecraft.entity.monster;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

final class IMob$1 implements IEntitySelector {

    IMob$1() {}

    @Override
    public boolean func_82704_a(Entity entity) {
        return entity instanceof IMob;
    }
}
