package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEnderPearl extends EntityThrowable {

    public EntityEnderPearl(World world) {
        super(world);
    }

    public EntityEnderPearl(World world, EntityLivingBase entitylivingbase) {
        super(world, entitylivingbase);
    }

    @Override
    protected void func_70184_a(MovingObjectPosition movingobjectposition) {
        if (movingobjectposition.field_72308_g != null) {
            movingobjectposition.field_72308_g.func_70097_a(DamageSource.func_76356_a(this, this.func_85052_h()), 0.0F);
        }

        for (int i = 0; i < 32; ++i) {
            this.field_70170_p.func_72869_a("portal", this.field_70165_t, this.field_70163_u + this.field_70146_Z.nextDouble() * 2.0D, this.field_70161_v, this.field_70146_Z.nextGaussian(), 0.0D, this.field_70146_Z.nextGaussian());
        }

        if (!this.field_70170_p.field_72995_K) {
            if (this.func_85052_h() != null && this.func_85052_h() instanceof EntityPlayerMP) {
                EntityPlayerMP entityplayermp = (EntityPlayerMP) this.func_85052_h();

                if (entityplayermp.field_71135_a.func_147362_b().func_150724_d() && entityplayermp.field_70170_p == this.field_70170_p) {
                    if (this.func_85052_h().func_70115_ae()) {
                        this.func_85052_h().func_70078_a((Entity) null);
                    }

                    this.func_85052_h().func_70634_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
                    this.func_85052_h().field_70143_R = 0.0F;
                    this.func_85052_h().func_70097_a(DamageSource.field_76379_h, 5.0F);
                }
            }

            this.func_70106_y();
        }

    }
}
