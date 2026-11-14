package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySnowball extends EntityThrowable {

    public EntitySnowball(World world) {
        super(world);
    }

    public EntitySnowball(World world, EntityLivingBase entitylivingbase) {
        super(world, entitylivingbase);
    }

    public EntitySnowball(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    protected void func_70184_a(MovingObjectPosition movingobjectposition) {
        int i;

        if (movingobjectposition.field_72308_g != null) {
            i = 0;
            if (movingobjectposition.field_72308_g instanceof EntityBlaze) {
                i = 3;
            }

            movingobjectposition.field_72308_g.func_70097_a(DamageSource.func_76356_a(this, this.func_85052_h()), (float) i);
        }

        for (i = 0; i < 8; ++i) {
            this.field_70170_p.func_72869_a("snowballpoof", this.field_70165_t, this.field_70163_u, this.field_70161_v, 0.0D, 0.0D, 0.0D);
        }

        if (!this.field_70170_p.field_72995_K) {
            this.func_70106_y();
        }

    }
}
