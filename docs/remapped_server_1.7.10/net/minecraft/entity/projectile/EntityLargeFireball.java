package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityLargeFireball extends EntityFireball {

    public int field_92057_e = 1;

    public EntityLargeFireball(World world) {
        super(world);
    }

    public EntityLargeFireball(World world, EntityLivingBase entitylivingbase, double d0, double d1, double d2) {
        super(world, entitylivingbase, d0, d1, d2);
    }

    @Override
    protected void func_70227_a(MovingObjectPosition movingobjectposition) {
        if (!this.field_70170_p.field_72995_K) {
            if (movingobjectposition.field_72308_g != null) {
                movingobjectposition.field_72308_g.func_70097_a(DamageSource.func_76362_a(this, this.field_70235_a), 6.0F);
            }

            this.field_70170_p.func_72885_a((Entity) null, this.field_70165_t, this.field_70163_u, this.field_70161_v, (float) this.field_92057_e, true, this.field_70170_p.func_82736_K().func_82766_b("mobGriefing"));
            this.func_70106_y();
        }

    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        nbttagcompound.func_74768_a("ExplosionPower", this.field_92057_e);
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        if (nbttagcompound.func_150297_b("ExplosionPower", 99)) {
            this.field_92057_e = nbttagcompound.func_74762_e("ExplosionPower");
        }

    }
}
