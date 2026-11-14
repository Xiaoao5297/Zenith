package net.minecraft.entity.boss;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;

final class EntityWither$1 implements IEntitySelector {

    EntityWither$1() {}

    @Override
    public boolean func_82704_a(Entity entity) {
        return entity instanceof EntityLivingBase && ((EntityLivingBase) entity).func_70668_bt() != EnumCreatureAttribute.UNDEAD;
    }
}
