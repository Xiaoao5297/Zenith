package net.minecraft.entity.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

class EntityAINearestAttackableTarget$1 implements IEntitySelector {

    EntityAINearestAttackableTarget$1(EntityAINearestAttackableTarget entityainearestattackabletarget, IEntitySelector ientityselector) {
        this.field_111102_d = entityainearestattackabletarget;
        this.field_111103_c = ientityselector;
    }

    @Override
    public boolean func_82704_a(Entity entity) {
        return !(entity instanceof EntityLivingBase) ? false : (this.field_111103_c != null && !this.field_111103_c.func_82704_a(entity) ? false : this.field_111102_d.func_75296_a((EntityLivingBase) entity, false));
    }
}
