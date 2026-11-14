package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityTameable;

public class EntityAITargetNonTamed extends EntityAINearestAttackableTarget {

    private EntityTameable field_75310_g;

    public EntityAITargetNonTamed(EntityTameable entitytameable, Class oclass, int i, boolean flag) {
        super(entitytameable, oclass, i, flag);
        this.field_75310_g = entitytameable;
    }

    @Override
    public boolean func_75250_a() {
        return !this.field_75310_g.func_70909_n() && super.func_75250_a();
    }
}
