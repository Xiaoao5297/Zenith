package net.minecraft.entity.boss;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class EntityDragonPart extends Entity {

    public final IEntityMultiPart field_70259_a;
    public final String field_146032_b;

    public EntityDragonPart(IEntityMultiPart ientitymultipart, String s, float f, float f1) {
        super(ientitymultipart.func_82194_d());
        this.func_70105_a(f, f1);
        this.field_70259_a = ientitymultipart;
        this.field_146032_b = s;
    }

    @Override
    protected void func_70088_a() {}

    @Override
    protected void func_70037_a(NBTTagCompound nbttagcompound) {}

    @Override
    protected void func_70014_b(NBTTagCompound nbttagcompound) {}

    @Override
    public boolean func_70067_L() {
        return true;
    }

    @Override
    public boolean func_70097_a(DamageSource damagesource, float f) {
        return this.func_85032_ar() ? false : this.field_70259_a.func_70965_a(this, damagesource, f);
    }

    @Override
    public boolean func_70028_i(Entity entity) {
        return this == entity || this.field_70259_a == entity;
    }
}
