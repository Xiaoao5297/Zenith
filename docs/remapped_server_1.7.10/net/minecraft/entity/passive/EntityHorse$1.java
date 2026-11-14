package net.minecraft.entity.passive;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

final class EntityHorse$1 implements IEntitySelector {

    EntityHorse$1() {}

    @Override
    public boolean func_82704_a(Entity entity) {
        return entity instanceof EntityHorse && ((EntityHorse) entity).func_110205_ce();
    }
}
